package com.whatpl.project.repository;

import com.whatpl.project.domain.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    @Query("select p from Project p left join fetch p.recruitJobs where p.id = :id")
    Optional<Project> findByIdWithRecruitJobs(Long id);
}
