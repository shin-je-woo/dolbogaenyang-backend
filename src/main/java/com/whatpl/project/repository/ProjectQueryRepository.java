package com.whatpl.project.repository;

import com.whatpl.project.domain.Project;

import java.util.Optional;

public interface ProjectQueryRepository {
    Optional<Project> findProjectWithAllById(Long id);
}
