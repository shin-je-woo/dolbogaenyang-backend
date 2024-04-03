package com.whatpl.member.repository;

import com.whatpl.member.domain.Member;
import com.whatpl.member.domain.SocialType;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;

import java.util.Optional;

public interface MemberQueryRepository {

    Optional<Member> findMemberWithAllById(long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Member> findMemberWithAllBySocialTypeId(SocialType socialType, String socialId);
}
