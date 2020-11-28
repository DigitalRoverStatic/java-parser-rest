package ru.leadersofdigital.digitalrover.parser.model;

import io.swagger.v3.oas.annotations.Parameter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SbrDto {

    private String subjectId;

    private String name;

    private String powerSystemId;

    @Parameter(description = "МВт*ч")
    private Integer IBR_ActualConsumption;

    @Parameter(description = "МВт*ч")
    private Integer IBR_ActualGeneration;

    @Parameter(description = "руб./МВт*ч")
    private Integer IBR_AveragePrice;

    private List<Pair<String, String>> siblings = new ArrayList<>();

    private List<SbrNodeDto> nodes = new ArrayList<>();

    private List<SubAreaDto> subAreas = new ArrayList<>();
}
