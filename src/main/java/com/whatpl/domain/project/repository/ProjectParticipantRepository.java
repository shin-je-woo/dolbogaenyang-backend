package com.whatpl.domain.project.repository;

import com.whatpl.global.common.model.Job;
import com.whatpl.domain.project.domain.ProjectParticipant;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProjectParticipantRepository extends JpaRepository<ProjectParticipant, Long> {

    int countByProjectIdAndJob(Long projectId, Job job);

    @EntityGraph(attributePaths = {"project", "participant"})
    Optional<ProjectParticipant> findWithAllById(Long participantId);
}
