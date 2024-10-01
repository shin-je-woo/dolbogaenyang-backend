package com.whatpl.domain.member.repository;

import com.whatpl.domain.member.domain.MemberPortfolio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface MemberPortfolioRepository extends JpaRepository<MemberPortfolio, Long> {

    @Query("select p from MemberPortfolio p left join fetch p.attachment where p.id = :id")
    Optional<MemberPortfolio> findByIdWithAttachment(Long id);
}
