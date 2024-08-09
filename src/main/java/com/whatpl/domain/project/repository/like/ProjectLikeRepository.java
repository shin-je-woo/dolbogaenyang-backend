package com.whatpl.domain.project.repository.like;

import com.whatpl.domain.project.domain.Project;
import com.whatpl.domain.project.domain.ProjectLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProjectLikeRepository extends JpaRepository<ProjectLike, Long> {

    Optional<ProjectLike> findByProjectIdAndMemberId(Long projectId, Long memberId);

    long countByProject(Project project);

    boolean existsByProjectIdAndMemberId(Long projectId, Long memberId);
}
