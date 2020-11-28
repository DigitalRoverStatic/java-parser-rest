package ru.leadersofdigital.digitalrover.parser.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.leadersofdigital.digitalrover.parser.domain.model.SubjectEntity;

public interface SubjectRepository extends JpaRepository<SubjectEntity, Long> {
}
