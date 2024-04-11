package com.whatpl.project.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.whatpl.project.domain.Apply;
import com.whatpl.project.domain.Project;
import com.whatpl.project.domain.enums.ApplyStatus;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.whatpl.member.domain.QMember.member;
import static com.whatpl.project.domain.QApply.apply;

@RequiredArgsConstructor
public class ApplyQueryRepositoryImpl implements ApplyQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Apply> findAllParticipants(Project project) {
        return queryFactory.selectFrom(apply)
                .leftJoin(apply.applicant, member).fetchJoin()
                .where(apply.project.eq(project))
                .where(apply.status.eq(ApplyStatus.ACCEPTED))
                .fetch();
    }
}
