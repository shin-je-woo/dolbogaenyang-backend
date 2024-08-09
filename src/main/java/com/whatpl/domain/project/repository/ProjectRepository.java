package com.whatpl.domain.project.repository;

import com.whatpl.domain.project.domain.Project;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long>, ProjectQueryRepository {

    @Query("select p from Project p left join fetch p.recruitJobs where p.id = :id")
    Optional<Project> findWithRecruitJobsById(Long id);

    @EntityGraph(attributePaths = {"writer"})
    Optional<Project> findWithWriterById(Long id);
}
