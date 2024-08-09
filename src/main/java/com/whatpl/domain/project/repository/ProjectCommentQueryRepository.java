package com.whatpl.domain.project.repository;

import com.whatpl.domain.project.domain.ProjectComment;

import java.util.List;

public interface ProjectCommentQueryRepository {
    List<ProjectComment> findWithAllByProjectId(Long projectId);
}
