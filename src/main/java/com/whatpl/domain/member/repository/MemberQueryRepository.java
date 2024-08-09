package com.whatpl.domain.member.repository;

import com.whatpl.domain.member.domain.Member;
import com.whatpl.domain.member.model.SocialType;

import java.util.Optional;

public interface MemberQueryRepository {

    Optional<Member> findMemberWithAllById(long id);

    Optional<Member> findMemberWithAllBySocialTypeId(SocialType socialType, String socialId);
}
