package com.whatpl.project.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.whatpl.project.domain.Project;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

import static com.whatpl.attachment.domain.QAttachment.attachment;
import static com.whatpl.member.domain.QMember.member;
import static com.whatpl.project.domain.QProject.project;
import static com.whatpl.project.domain.QProjectSkill.projectSkill;
import static com.whatpl.project.domain.QRecruitJob.recruitJob;

@RequiredArgsConstructor
public class ProjectQueryRepositoryImpl implements ProjectQueryRepository{

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<Project> findProjectWithAllById(Long id) {
        Project resultProject = queryFactory.selectFrom(project)
                .leftJoin(project.projectSkills, projectSkill).fetchJoin()
                .leftJoin(project.recruitJobs, recruitJob).fetchJoin()
                .leftJoin(project.representImage, attachment).fetchJoin()
                .leftJoin(project.writer, member).fetchJoin()
                .where(project.id.eq(id))
                .fetchOne();
        return Optional.ofNullable(resultProject);
    }
}
