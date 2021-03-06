package ru.leadersofdigital.digitalrover.parser.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.leadersofdigital.digitalrover.parser.model.SbrDto;
import ru.leadersofdigital.digitalrover.parser.service.SbrService;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.IntStream;

@RequestMapping("/sbr")
@RequiredArgsConstructor
@RestController
public class SbrController {

    private final SbrService sbrService;

    @PostMapping("/parse")
    public SbrDto parse(@RequestParam @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate localDate,
                        @RequestParam Integer hour,
                        @RequestParam String subjectId,
                        @RequestParam String powerSystemId
    ) {
        return sbrService.parseSbrLvl2(localDate, hour, subjectId, powerSystemId);
    }

    @PostMapping("/parse/root")
    public SbrDto parseRoot(@RequestParam @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate localDate,
                            @RequestParam Integer hour) {
        return sbrService.parseRoot(localDate, hour);
    }

    @PostMapping("/parse/oes")
    public List<SbrDto> parseOes(@RequestParam @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate localDate,
                                 @RequestParam Integer hour) {
        return sbrService.parseOes(localDate, hour);
    }

    @PostMapping("/import")
    public void importData(@RequestParam @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate localDate,
                           @RequestParam Integer hour) {
        sbrService.saveToDb(localDate, hour);
    }

    @PostMapping("/import/day")
    public void importData(@RequestParam @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate localDate) {
        IntStream.range(0, 23).forEach(hour ->
                sbrService.saveToDb(localDate, hour)
        );
    }

    @PostMapping("/parseCsv/history")
    public void parseCsv() throws IOException {
        sbrService.parseCsvHistory();
    }

    @PostMapping("/parseCsv/history/oas")
    public void parseCsvOas() throws IOException {
        sbrService.parseCsvHistoryOas();
    }
}
