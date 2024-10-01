package com.whatpl.domain.project.repository.project;

import com.whatpl.domain.member.domain.Member;
import com.whatpl.domain.project.domain.Project;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long>, ProjectQueryRepository {

    @Query("select p from Project p left join fetch p.recruitJobs where p.id = :id")
    Optional<Project> findWithRecruitJobsById(Long id);

    @EntityGraph(attributePaths = {"writer"})
    Optional<Project> findWithWriterById(Long id);

    @Query("select p from Project p left join fetch p.projectParticipants pp where pp.participant = :member")
    List<Project> findParticipatedProjects(Member member);

    @Query("select p from Project p left join fetch p.representImage where p.representImage.id = :representImageId")
    Optional<Project> findByRepresentImageId(Long representImageId);
}
