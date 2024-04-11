package com.whatpl.project.repository;

import com.whatpl.member.domain.Member;
import com.whatpl.project.domain.Apply;
import com.whatpl.project.domain.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ApplyRepository extends JpaRepository<Apply, Long>, ApplyQueryRepository {

    Optional<Apply> findByProjectAndApplicant(Project project, Member applicant);

    @Query("select a from Apply a left join fetch a.project left join fetch a.applicant where a.id = :id")
    Optional<Apply> findWithProjectAndApplicantById(Long id);
}
