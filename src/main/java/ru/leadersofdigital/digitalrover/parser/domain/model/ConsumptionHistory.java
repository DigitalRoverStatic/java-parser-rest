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
@Table(name = "history")
public class ConsumptionHistory extends BaseEntity<Long> {

    @Column
    private LocalDate localDate;

    @Column
    private String name;

    @Column
    private Double consumption;

}
