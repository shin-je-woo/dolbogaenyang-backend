package com.whatpl.project.repository;

import com.whatpl.global.common.domain.enums.Job;
import com.whatpl.project.domain.ProjectParticipant;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProjectParticipantRepository extends JpaRepository<ProjectParticipant, Long> {

    @EntityGraph(attributePaths = {"project", "participant"})
    List<ProjectParticipant> findAllByProjectId(Long projectId);

    int countByProjectIdAndJob(Long projectId, Job job);

    @EntityGraph(attributePaths = {"project", "participant"})
    Optional<ProjectParticipant> findWithAllById(Long participantId);
}
