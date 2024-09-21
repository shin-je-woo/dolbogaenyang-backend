package com.whatpl.domain.whatplpople.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.whatpl.domain.member.domain.Member;
import com.whatpl.domain.member.domain.MemberSkill;
import com.whatpl.domain.member.domain.MemberSubject;
import com.whatpl.domain.project.domain.Project;
import com.whatpl.domain.project.domain.RecruitJob;
import com.whatpl.domain.project.model.ProjectStatus;
import com.whatpl.domain.whatplpople.dto.WhatplpeopleDto;
import com.whatpl.domain.whatplpople.dto.WhatplpeopleSearchCondition;
import com.whatpl.global.common.model.Job;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import com.querydsl.jpa.impl.JPAQueryFactory;

import static com.querydsl.core.types.Projections.fields;
import static com.querydsl.jpa.JPAExpressions.select;
import static com.querydsl.jpa.JPAExpressions.selectFrom;
import static com.whatpl.domain.member.domain.QMember.member;
import static com.whatpl.domain.member.domain.QMemberSkill.memberSkill;
import static com.whatpl.domain.member.domain.QMemberSubject.memberSubject;
import static com.whatpl.domain.project.domain.QProject.project;
import static com.whatpl.domain.project.domain.QProjectParticipant.projectParticipant;
import static com.whatpl.domain.project.domain.QRecruitJob.recruitJob;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class WhatplepeopleQueryRepositoryImpl implements WhatplepeopleQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<WhatplpeopleDto> findWhatplpeopleMe(Pageable pageable, WhatplpeopleSearchCondition whatplpeopleSearchCondition) {
        List<Project> myProjects = queryFactory.selectFrom(project)
                .leftJoin(project.recruitJobs, recruitJob)
                .leftJoin(project.writer, member).fetchJoin()
                .where(project.writer.id.eq(whatplpeopleSearchCondition.getLoginMemberId()),
                        project.status.eq(ProjectStatus.RECRUITING),
                        recruitJobEq())
                .fetch();

        List<Job> searchJob = new ArrayList<Job>();

        myProjects.forEach(myProject-> {
            myProject.getRecruitJobs().forEach(projectJob->{
                searchJob.add(projectJob.getJob());
            });
        });

        return queryFactory.selectFrom(member)
                .leftJoin(member.memberSubjects, memberSubject)
                .leftJoin(member.memberSkills, memberSkill)
                .where(member.job.in(searchJob))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch()
                .stream()
                .map(tuple -> new WhatplpeopleDto(
                        tuple.getId(),
                        tuple.getNickname(),
                        tuple.getJob(),
                        tuple.getCareer(),
                        tuple.getMemberSkills().stream().map(MemberSkill::getSkill).toList(),
                        tuple.getMemberSubjects().stream().map(MemberSubject::getSubject).toList()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public List<WhatplpeopleDto> findMeWhatplpeople(Pageable pageable, WhatplpeopleSearchCondition whatplpeopleSearchCondition) {
        return queryFactory.select(fields(WhatplpeopleDto.class,
                        recruitJob.project.writer.id.as("memberId"),
                        recruitJob.project.writer.nickname.as("nickname")
                        ))
                .from(recruitJob)
                .leftJoin(recruitJob.project, project)
                .leftJoin(project.writer, member).fetchJoin()
                .where(recruitJob.job.eq(whatplpeopleSearchCondition.getJob()),
                        project.status.eq(ProjectStatus.RECRUITING),
                        recruitJobEq())
                .distinct()
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();
    }

    @Override
    public List<WhatplpeopleDto> findAllWhatplpeople(Pageable pageable, WhatplpeopleSearchCondition whatplpeopleSearchCondition) {
        return queryFactory.select(fields(WhatplpeopleDto.class,
                        member.id.as("memberId"),
                        member.nickname.as("nickname"),
                        member.career.as("career")
                )).from(member)
                .leftJoin(member.memberSubjects, memberSubject)
                .leftJoin(member.memberSkills, memberSkill)
                .where(member.job.eq(whatplpeopleSearchCondition.getJob()),
                        member.memberSubjects.any().subject.in(whatplpeopleSearchCondition.getSubjects()),
                        member.memberSkills.any().skill.in(whatplpeopleSearchCondition.getSkill()),
                        member.nickname.like(whatplpeopleSearchCondition.getKeyword()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();
    }

    private BooleanExpression recruitJobEq(){
        return select(projectParticipant.count())
                .from(projectParticipant)
                .where(projectParticipant.project.eq(project))
                .lt(select(recruitJob.recruitAmount.longValue())
                        .from(recruitJob)
                        .where(recruitJob.project.eq(project)));
    }
}
