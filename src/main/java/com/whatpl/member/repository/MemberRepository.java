package com.whatpl.member.repository;

import com.whatpl.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberQueryRepository{
    Optional<Member> findByNickname(String nickname);
}
