package com.whatpl.project.repository;

import com.whatpl.member.domain.Member;
import com.whatpl.project.domain.Apply;
import com.whatpl.project.domain.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ApplyRepository extends JpaRepository<Apply, Long> {

    Optional<Apply> findByProjectAndApplicant(Project project, Member applicant);
}
