package ru.leadersofdigital.digitalrover.parser.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "consumptions")
public class ConsumptionEntity extends BaseEntity<Long> {

    @Column
    private String subjectId;

    @Column
    private String name;

    @Column
    private Integer IBR_ActualConsumption;

    @Column
    private Integer IBR_ActualGeneration;

    @Column
    private Integer IBR_AveragePrice;

    @Column
    private LocalDate localDate;

    @Column
    private Integer hour;
}
