package com.whatpl.domain.project.repository.apply;

import com.whatpl.domain.member.domain.Member;
import com.whatpl.domain.project.domain.Apply;
import com.whatpl.global.common.model.ApplyStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ApplyRepository extends JpaRepository<Apply, Long> {

    @Query("select a from Apply a left join fetch a.project left join fetch a.applicant where a.id = :id")
    Optional<Apply> findWithProjectAndApplicantById(Long id);

    @Query("select a from Apply a where a.applicant = :applicant and a.status = :status")
    Optional<Apply> findByApplicantAndStatus(Member applicant, ApplyStatus status);
}
