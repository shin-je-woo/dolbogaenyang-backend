package com.whatpl.member.repository;

import com.whatpl.member.domain.Member;
import com.whatpl.member.domain.SocialType;

import java.util.Optional;

public interface MemberQueryRepository {

    Optional<Member> findMemberWithAllById(long id);

    Optional<Member> findMemberWithAllBySocialTypeId(SocialType socialType, String socialId);
}
