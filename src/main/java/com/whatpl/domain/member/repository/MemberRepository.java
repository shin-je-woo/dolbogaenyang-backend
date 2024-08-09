package com.whatpl.domain.member.repository;

import com.whatpl.domain.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberQueryRepository{
    Optional<Member> findByNickname(String nickname);
}
