package com.whatpl.project.repository;

import com.whatpl.project.domain.Project;
import com.whatpl.project.dto.ProjectInfo;
import com.whatpl.project.dto.ProjectSearchCondition;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.Optional;

public interface ProjectQueryRepository {

    Optional<Project> findProjectWithAllById(Long id);

    Slice<ProjectInfo> search(Pageable pageable, ProjectSearchCondition searchCondition);
}
