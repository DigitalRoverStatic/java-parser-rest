package ru.leadersofdigital.digitalrover.parser.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.leadersofdigital.digitalrover.parser.domain.model.OasHistoryEntity;

public interface OasHistoryRepository extends JpaRepository<OasHistoryEntity, Long> {
}
