package ru.leadersofdigital.digitalrover.parser.model;

import io.swagger.v3.oas.annotations.Parameter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SubAreaDto {

    private String subjectId;

    private String name;

    private String powerSystemId;

    @Parameter(description = "МВт*ч")
    private Integer IBR_ActualConsumption;

    @Parameter(description = "МВт*ч")
    private Integer IBR_ActualGeneration;

    @Parameter(description = "руб./МВт*ч")
    private Integer IBR_AveragePrice;
}
