package ru.leadersofdigital.digitalrover.parser.service;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import de.siegmar.fastcsv.reader.CsvContainer;
import de.siegmar.fastcsv.reader.CsvReader;
import de.siegmar.fastcsv.reader.CsvRow;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.json.JSONArray;
import org.json.JSONObject;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.leadersofdigital.digitalrover.parser.domain.model.ConsumptionEntity;
import ru.leadersofdigital.digitalrover.parser.domain.model.ConsumptionHistory;
import ru.leadersofdigital.digitalrover.parser.domain.model.OasHistoryEntity;
import ru.leadersofdigital.digitalrover.parser.domain.repository.ConsumptionHistoryRepository;
import ru.leadersofdigital.digitalrover.parser.domain.repository.ConsumptionRepository;
import ru.leadersofdigital.digitalrover.parser.domain.repository.OasHistoryRepository;
import ru.leadersofdigital.digitalrover.parser.domain.repository.SubjectRepository;
import ru.leadersofdigital.digitalrover.parser.exception.CustomException;
import ru.leadersofdigital.digitalrover.parser.model.SbrDto;
import ru.leadersofdigital.digitalrover.parser.model.SbrNodeDto;
import ru.leadersofdigital.digitalrover.parser.model.SubAreaDto;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class SbrService {

    private static final String SBR_MAP_REQUEST_URL = "http://br.so-ups.ru/webapi/api/map/MapPartial";
    private static final String MAP_TYPE = "0";
    private static final String DATE_FORMAT = "yyyy-MM-dd";

    private final SubjectRepository subjectRepository;
    private final ConsumptionHistoryRepository consumptionHistoryRepository;
    private final OasHistoryRepository oasHistoryRepository;
    private final ConsumptionRepository consumptionRepository;
    private final ModelMapper mapper;

    public void saveToDb(LocalDate localDate, Integer hour) {
        try {
            List<SbrDto> sbrDtos = parseOes(localDate, hour);
            for (SbrDto sbrDto : sbrDtos) {
                HttpResponse<JsonNode> response = getData(localDate, hour, sbrDto.getSubjectId(), sbrDto.getPowerSystemId());
                SbrDto parsed = parse(response.getBody());
                for (SubAreaDto subArea : parsed.getSubAreas()) {
                    HttpResponse<JsonNode> data = getData(localDate, hour, subArea.getSubjectId(), subArea.getPowerSystemId());
                    SbrDto sbrLvl2 = parse(data.getBody());
                    ConsumptionEntity entity = new ConsumptionEntity();
                    mapper.map(sbrLvl2, entity);
                    entity.setLocalDate(localDate);
                    entity.setHour(hour);
                    consumptionRepository.save(entity);
                    log.info("SAVED: " + sbrLvl2.getSubjectId());
                }
            }
        } catch (UnirestException e) {
            log.error(e.getMessage(), e);
            throw new CustomException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public SbrDto parseRoot(LocalDate localDate, Integer hour) {
        try {
            HttpResponse<JsonNode> response = getData(localDate, hour, "", "");
            System.out.println(response.getBody());
            return parse(response.getBody());
        } catch (UnirestException e) {
            log.error(e.getMessage(), e);
            throw new CustomException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public SbrDto parseSbrLvl2(LocalDate localDate, Integer hour, String subjectId, String powerSystemId) {
        try {
            HttpResponse<JsonNode> response = getData(localDate, hour, subjectId, powerSystemId);
            System.out.println(response.getBody());
            return parse(response.getBody());
        } catch (UnirestException e) {
            log.error(e.getMessage(), e);
            throw new CustomException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public List<SbrDto> parseOes(LocalDate localDate, Integer hour) {
        try {
            HttpResponse<JsonNode> response = getData(localDate, hour, "", "");
            System.out.println(response.getBody());
            SbrDto parsed = parse(response.getBody());
            List<SbrDto> result = new ArrayList<>();
            parsed.getSubAreas().parallelStream().forEach(subAreaDto -> {
                try {
                    HttpResponse<JsonNode> resp = getData(localDate, hour, subAreaDto.getSubjectId(), subAreaDto.getPowerSystemId());
                    result.add(parse(resp.getBody()));
                } catch (UnirestException e) {
                    log.error(e.getMessage());
                }
            });
            return result;
        } catch (UnirestException e) {
            log.error(e.getMessage(), e);
            throw new CustomException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private HttpResponse<JsonNode> getData(LocalDate localDate, Integer hour, String subjectId, String powerSystemId) throws UnirestException {
        return Unirest.get(SBR_MAP_REQUEST_URL)
                .queryString("MapType", MAP_TYPE)
                .queryString("Date", DateTimeFormatter.ofPattern(DATE_FORMAT).format(localDate))
                .queryString("Hour", hour)
                .queryString("PowerSystemId", powerSystemId)
                .queryString("SubjectId", subjectId)
                .asJson();
    }

    private SbrDto parse(JsonNode jsonNode) {
        return new SbrDto(
                (((JSONObject) jsonNode.getObject().get("MainArea")).get("SubjectId").toString()),
                (((JSONObject) jsonNode.getObject().get("MainArea")).get("Name").toString()),
                ((JSONObject) jsonNode.getObject().get("MainArea")).get("PowerSystemId").toString(),
                parseInt(((JSONObject) jsonNode.getObject().get("MainArea")).get("IBR_ActualConsumption").toString().replaceAll("МВт\\*ч", "").replaceAll(" ", "")),
                parseInt(((JSONObject) jsonNode.getObject().get("MainArea")).get("IBR_ActualGeneration").toString().replaceAll("МВт\\*ч", "").replaceAll(" ", "")),
                parseInt(((JSONObject) jsonNode.getObject().get("MainArea")).get("IBR_AveragePrice").toString().replaceAll("руб\\./МВт\\*ч", "").replaceAll(" ", "")),
                StreamSupport.stream(Spliterators.spliteratorUnknownSize(((JSONArray) jsonNode.getObject().get("SiblingAreas")).iterator(),
                        0), false).parallel()
                        .map(sibling -> Pair.of(((JSONObject) sibling).get("SubjectId").toString(), ((JSONObject) sibling).get("Name").toString()))
                        .collect(Collectors.toList()),
                StreamSupport.stream(Spliterators.spliteratorUnknownSize(((JSONArray) jsonNode.getObject().get("Nodes")).iterator(),
                        0), false).parallel()
                        .map(node -> new SbrNodeDto(
                                parseInt(((JSONObject) node).get("Id").toString()),
                                parseInt(((JSONObject) node).get("NodeType").toString()),
                                ((JSONObject) node).get("Name").toString(),
                                parseInt(((JSONObject) node).get("Ibr").toString().replaceAll("руб\\./МВт\\*ч", "").replaceAll(" ", ""))
                        )).collect(Collectors.toList()),

                StreamSupport.stream(Spliterators.spliteratorUnknownSize(((JSONArray) jsonNode.getObject().get("SubAreas")).iterator(),
                        0), false).parallel()
                        .map(subArea -> new SubAreaDto(
                                ((JSONObject) subArea).get("SubjectId").toString(),
                                ((JSONObject) subArea).get("Name").toString(),
                                ((JSONObject) subArea).get("PowerSystemId").toString(),
                                parseInt(((JSONObject) subArea).get("IBR_ActualConsumption").toString().replaceAll("МВт\\*ч", "").replaceAll(" ", "")),
                                parseInt(((JSONObject) subArea).get("IBR_ActualGeneration").toString().replaceAll("МВт\\*ч", "").replaceAll(" ", "")),
                                parseInt(((JSONObject) subArea).get("IBR_AveragePrice").toString().replaceAll("руб\\./МВт\\*ч", "").replaceAll(" ", ""))
                        )).collect(Collectors.toList())
        );
    }

    private Integer parseInt(String s) {
        try {
            return Integer.valueOf(s);
        } catch (Exception e) {
            return 0;
        }
    }

    public void parseCsvHistory() throws IOException {
        File file = new File("./input/ce.csv");
        CsvReader csvReader = new CsvReader();
        csvReader.setContainsHeader(true);
        csvReader.setFieldSeparator(';');
        Integer year = 0, mouth = 0, day = 0;
        CsvContainer csv = csvReader.read(file, StandardCharsets.UTF_8);
        for (CsvRow row : csv.getRows()) {
            if (!row.getField(0).isEmpty()) {
                year = Integer.valueOf(row.getField(0));
            }
            if (!row.getField(1).isEmpty()) {
                mouth = Integer.valueOf(row.getField(1));
            }
            day = Integer.valueOf(row.getField(2));
            for (int index = 3; index < row.getFields().size(); index++) {
                if (!row.getField(index).isEmpty()) {
                    consumptionHistoryRepository.save(new ConsumptionHistory(
                            LocalDate.of(year, mouth, day),
                            String.valueOf(row.getFieldMap().keySet().toArray()[index]),
                            Double.valueOf(row.getField(index))
                    ));
                }
            }
        }
    }

    public void parseCsvHistoryOas() throws IOException {
        File file = new File("./input/ce.csv");
        CsvReader csvReader = new CsvReader();
        csvReader.setContainsHeader(true);
        csvReader.setFieldSeparator(';');
        Integer year = 0, mouth = 0, day = 0;
        CsvContainer csv = csvReader.read(file, StandardCharsets.UTF_8);
        for (CsvRow row : csv.getRows()) {
            if (!row.getField(0).isEmpty()) {
                year = Integer.valueOf(row.getField(0));
            }
            if (!row.getField(1).isEmpty()) {
                mouth = Integer.valueOf(row.getField(1));
            }
            day = Integer.valueOf(row.getField(2));
            for (int index = 3; index < row.getFields().size(); index++) {
                if (!row.getField(index).isEmpty()) {
                    oasHistoryRepository.save(new OasHistoryEntity(
                            LocalDate.of(year, mouth, day),
                            String.valueOf(row.getFieldMap().keySet().toArray()[index]),
                            Double.valueOf(row.getField(index))
                    ));
                }
            }
        }
    }
}
