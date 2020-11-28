package ru.leadersofdigital.digitalrover.parser.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.leadersofdigital.digitalrover.parser.util.Percentage;
import ru.tinkoff.invest.openapi.SandboxOpenApi;
import ru.tinkoff.invest.openapi.models.market.CandleInterval;
import ru.tinkoff.invest.openapi.models.market.HistoricalCandles;
import ru.tinkoff.invest.openapi.okhttp.OkHttpOpenApiFactory;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class InvestService {

    public List<HistoricalCandles> getAll(String token) throws ExecutionException, InterruptedException {
        OkHttpOpenApiFactory factory = new OkHttpOpenApiFactory(token, Logger.getLogger("Invest"));

        SandboxOpenApi api = factory.createSandboxOpenApiClient(Executors.newSingleThreadExecutor());
        // ОБЯЗАТЕЛЬНО нужно выполнить регистрацию в "песочнице"
        api.getSandboxContext().performRegistration(null).join();


        List<String> figis = api.getMarketContext().getMarketStocks().get().instruments.stream().map(instrument -> instrument.figi).collect(Collectors.toList());

        LocalDateTime finish = LocalDateTime.now();
        LocalDateTime start = finish.minusMonths(1);

        Percentage percentage = new Percentage(figis.size(), "Progress: ");
        CopyOnWriteArrayList<HistoricalCandles> result = new CopyOnWriteArrayList<>();
        figis.forEach(figi -> {
            try {
                result.add(api.getMarketContext().getMarketCandles(
                        figi,
                        OffsetDateTime.of(start, ZoneOffset.UTC),
                        OffsetDateTime.of(finish, ZoneOffset.UTC),
                        CandleInterval.DAY).get().orElseThrow());
                percentage.increment();
            } catch (InterruptedException | ExecutionException e) {
                log.error(e.getMessage());
            }
        });

        return result;
    }
}
