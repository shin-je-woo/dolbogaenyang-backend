package com.whatpl.project.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.whatpl.project.domain.ProjectComment;
import com.whatpl.project.domain.QProjectComment;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.whatpl.member.domain.QMember.member;
import static com.whatpl.project.domain.QProject.project;
import static com.whatpl.project.domain.QProjectComment.projectComment;

@RequiredArgsConstructor
public class ProjectCommentQueryRepositoryImpl implements ProjectCommentQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<ProjectComment> findWithAllByProjectId(Long projectId) {
        QProjectComment children = new QProjectComment("children");
        return queryFactory.selectFrom(projectComment)
                .leftJoin(projectComment.writer, member).fetchJoin()
                .leftJoin(projectComment.project, project).fetchJoin()
                .leftJoin(projectComment.children, children).fetchJoin()
                .where(
                        projectComment.project.id.eq(projectId)
                )
                .orderBy(
                        projectComment.createdAt.asc()
                )
                .fetch();
    }
}
