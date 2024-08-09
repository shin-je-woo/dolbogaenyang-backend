package com.whatpl.domain.project.repository;

import com.whatpl.domain.project.domain.ProjectComment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectCommentRepository extends JpaRepository<ProjectComment, Long>, ProjectCommentQueryRepository {
}
