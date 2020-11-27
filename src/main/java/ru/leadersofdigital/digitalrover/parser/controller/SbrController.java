package ru.leadersofdigital.digitalrover.parser.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.leadersofdigital.digitalrover.parser.model.SbrDto;
import ru.leadersofdigital.digitalrover.parser.service.SbrService;

import java.time.LocalDate;
import java.util.List;

@RequestMapping("/sbr")
@RequiredArgsConstructor
@RestController
public class SbrController {

    private final SbrService sbrService;

    @PostMapping("/parse")
    public SbrDto parse(@RequestParam @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate localDate,
                        @RequestParam Integer hour,
                        @RequestParam String subjectId) {
        return sbrService.parseSbrLvl2(localDate, hour, subjectId);
    }

    @PostMapping("/parse/root")
    public SbrDto parseRoot(@RequestParam @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate localDate,
                        @RequestParam Integer hour) {
        return sbrService.parseRoot(localDate, hour);
    }

    @PostMapping("/parse/oes")
    public List<SbrDto> parseOes(@RequestParam @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate localDate,
                              @RequestParam Integer hour){
        return sbrService.parseOes(localDate, hour);
    }
}
