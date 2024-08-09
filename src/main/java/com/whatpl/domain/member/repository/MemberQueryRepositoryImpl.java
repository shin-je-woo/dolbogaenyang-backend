package com.whatpl.domain.member.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.whatpl.domain.member.domain.Member;
import com.whatpl.domain.member.domain.enums.SocialType;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

import static com.whatpl.domain.member.domain.QMember.member;
import static com.whatpl.domain.member.domain.QMemberPortfolio.memberPortfolio;
import static com.whatpl.domain.member.domain.QMemberReference.memberReference;
import static com.whatpl.domain.member.domain.QMemberSkill.memberSkill;
import static com.whatpl.domain.member.domain.QMemberSubject.memberSubject;

@RequiredArgsConstructor
public class MemberQueryRepositoryImpl implements MemberQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<Member> findMemberWithAllById(long id) {
        Member result = queryFactory.selectFrom(member)
                .leftJoin(member.memberSkills, memberSkill).fetchJoin()
                .leftJoin(member.memberPortfolios, memberPortfolio).fetchJoin()
                .leftJoin(member.memberReferences, memberReference).fetchJoin()
                .leftJoin(member.memberSubjects, memberSubject).fetchJoin()
                .where(member.id.eq(id))
                .fetchOne();
        return Optional.ofNullable(result);
    }

    @Override
    public Optional<Member> findMemberWithAllBySocialTypeId(SocialType socialType, String socialId) {
        Member result = queryFactory.selectFrom(member)
                .leftJoin(member.memberSkills, memberSkill).fetchJoin()
                .leftJoin(member.memberPortfolios, memberPortfolio).fetchJoin()
                .leftJoin(member.memberReferences, memberReference).fetchJoin()
                .leftJoin(member.memberSubjects, memberSubject).fetchJoin()
                .where(member.socialType.eq(socialType), member.socialId.eq(socialId))
                .fetchOne();
        return Optional.ofNullable(result);
    }
}
