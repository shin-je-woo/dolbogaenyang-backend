package com.whatpl.project.repository;

import com.whatpl.project.domain.ProjectComment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectCommentRepository extends JpaRepository<ProjectComment, Long>, ProjectCommentQueryRepository {
}
