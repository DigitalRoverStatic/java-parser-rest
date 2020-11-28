package ru.leadersofdigital.digitalrover.parser.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.leadersofdigital.digitalrover.parser.service.InvestService;
import ru.tinkoff.invest.openapi.models.market.HistoricalCandles;

import java.util.List;
import java.util.concurrent.ExecutionException;

@RequestMapping("/invest")
@RequiredArgsConstructor
@RestController
public class InvestController {

    private final InvestService investService;

    @GetMapping("/all")
    public List<HistoricalCandles> gold(@RequestParam String token) throws ExecutionException, InterruptedException {
        return investService.getAll(token);
    }

}
