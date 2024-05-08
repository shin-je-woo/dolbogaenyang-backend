package com.whatpl.project.repository;

import com.whatpl.project.domain.ProjectParticipant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectParticipantRepository extends JpaRepository<ProjectParticipant, Long> {
}
