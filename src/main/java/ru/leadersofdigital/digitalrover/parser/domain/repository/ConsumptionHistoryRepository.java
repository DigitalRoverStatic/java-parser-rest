package ru.leadersofdigital.digitalrover.parser.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.leadersofdigital.digitalrover.parser.domain.model.ConsumptionHistory;

public interface ConsumptionHistoryRepository extends JpaRepository<ConsumptionHistory, Long> {
}
