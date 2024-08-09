package com.whatpl.domain.project.repository.participant;

import com.whatpl.domain.project.domain.ProjectParticipant;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProjectParticipantRepository extends JpaRepository<ProjectParticipant, Long> {

    @EntityGraph(attributePaths = {"project", "participant"})
    Optional<ProjectParticipant> findWithAllById(Long participantId);
}
