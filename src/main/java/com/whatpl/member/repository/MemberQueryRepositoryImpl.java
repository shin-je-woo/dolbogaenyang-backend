package com.whatpl.member.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.whatpl.member.domain.Member;
import com.whatpl.member.domain.SocialType;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

import static com.whatpl.member.domain.QMember.member;
import static com.whatpl.member.domain.QMemberSkill.memberSkill;

@RequiredArgsConstructor
public class MemberQueryRepositoryImpl implements MemberQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<Member> findMemberWithAllById(long id) {
        Member result = queryFactory.selectFrom(member)
                .join(member.memberSkills, memberSkill).fetchJoin()
                .where(member.id.eq(id))
                .fetchOne();
        return Optional.ofNullable(result);
    }

    @Override
    public Optional<Member> findMemberWithAllBySocialTypeId(SocialType socialType, String socialId) {
        Member result = queryFactory.selectFrom(member)
                .join(member.memberSkills, memberSkill).fetchJoin()
                .where(member.socialType.eq(socialType), member.socialId.eq(socialId))
                .fetchOne();
        return Optional.ofNullable(result);
    }
}
