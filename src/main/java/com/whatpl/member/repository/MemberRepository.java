package com.whatpl.member.repository;

import com.whatpl.member.domain.Member;
import com.whatpl.member.domain.SocialType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findBySocialTypeAndSocialId(SocialType socialType, String socialId);

    Optional<Member> findByNickname(String nickname);
}
