package com.whatpl.project.repository;

import com.whatpl.project.domain.ProjectComment;

import java.util.List;

public interface ProjectCommentQueryRepository {
    List<ProjectComment> findWithAllByProjectId(Long projectId);
}
