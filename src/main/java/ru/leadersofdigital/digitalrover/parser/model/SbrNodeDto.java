package ru.leadersofdigital.digitalrover.parser.model;

import io.swagger.v3.oas.annotations.Parameter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SbrNodeDto {

    private Integer id;

    private Integer nodeType;

    private String name;

    @Parameter(description = "руб./МВт*ч")
    private Integer Ibr;
}
