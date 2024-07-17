package com.whatpl.project.repository;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.whatpl.global.common.domain.enums.Job;
import com.whatpl.global.common.domain.enums.Skill;
import com.whatpl.global.common.domain.enums.Subject;
import com.whatpl.member.domain.QMember;
import com.whatpl.project.domain.*;
import com.whatpl.project.domain.enums.ProjectStatus;
import com.whatpl.project.dto.ProjectInfo;
import com.whatpl.project.dto.ProjectSearchCondition;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;

import static com.querydsl.core.types.Projections.fields;
import static com.querydsl.jpa.JPAExpressions.select;
import static com.querydsl.jpa.JPAExpressions.selectFrom;
import static com.whatpl.attachment.domain.QAttachment.attachment;
import static com.whatpl.member.domain.QMember.member;
import static com.whatpl.project.domain.QProject.project;
import static com.whatpl.project.domain.QProjectComment.projectComment;
import static com.whatpl.project.domain.QProjectLike.projectLike;
import static com.whatpl.project.domain.QProjectParticipant.projectParticipant;
import static com.whatpl.project.domain.QProjectSkill.projectSkill;
import static com.whatpl.project.domain.QRecruitJob.recruitJob;
import static java.util.stream.Collectors.groupingBy;

@RequiredArgsConstructor
public class ProjectQueryRepositoryImpl implements ProjectQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<Project> findProjectWithParticipantsById(Long id) {
        QMember participant = new QMember("participant");
        Project resultProject = queryFactory.selectFrom(project)
                .leftJoin(project.representImage, attachment).fetchJoin()
                .leftJoin(project.writer, member).fetchJoin()
                .leftJoin(project.projectParticipants, projectParticipant).fetchJoin()
                .leftJoin(projectParticipant.participant, participant).fetchJoin()
                .where(project.id.eq(id))
                .fetchOne();
        return Optional.ofNullable(resultProject);
    }

    @Override
    public List<ProjectInfo> findProjectInfos(Pageable pageable, ProjectSearchCondition searchCondition) {
        return queryFactory.select(fields(ProjectInfo.class,
                        project.id.as("projectId"),
                        project.title.as("title"),
                        project.status.as("status"),
                        project.subject.as("subject"),
                        project.profitable.as("profitable"),
                        project.views.as("vies"),
                        buildRepresentImageUri().as("representImageUri"),
                        myLikeExists(searchCondition).as("myLike")
                ))
                .from(project)
                .where(subjectEq(searchCondition.getSubject()),
                        jobEq(searchCondition.getJob(), searchCondition.getStatus()),
                        skillEq(searchCondition.getSkill()),
                        statusEq(searchCondition.getStatus()),
                        profitableEq(searchCondition.getProfitable()),
                        titleLike(searchCondition.getKeyword())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .orderBy(projectOrderBy(pageable))
                .fetch();
    }

    private OrderSpecifier<?>[] projectOrderBy(Pageable pageable) {
        List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();
        for (Sort.Order order : pageable.getSort()) {
            switch (order.getProperty().toLowerCase()) {
                case "latest" -> { // 최신순
                    OrderSpecifier<LocalDateTime> latest = new OrderSpecifier<>(Order.DESC, project.createdAt);
                    orderSpecifiers.add(latest);
                }
                case "popular" -> { // 인기순
                    OrderSpecifier<Long> popular = new OrderSpecifier<>(Order.DESC, project.views);
                    orderSpecifiers.add(popular);
                }
                default -> {
                    OrderSpecifier<LocalDateTime> latest = new OrderSpecifier<>(Order.DESC, project.createdAt);
                    orderSpecifiers.add(latest);
                }
            }
        }
        return orderSpecifiers.toArray(new OrderSpecifier[0]);
    }

    @Override
    public Map<Long, List<ProjectSkill>> findProjectSkillMap(List<Long> projectIds) {
        List<ProjectSkill> projectSkills = queryFactory.selectFrom(projectSkill)
                .where(projectSkill.project.id.in(projectIds))
                .fetch();
        return projectSkills.stream()
                .collect(groupingBy(projectSkill -> projectSkill.getProject().getId()));
    }

    @Override
    public Map<Long, List<RecruitJob>> findRecruitJobMap(List<Long> projectIds) {
        List<RecruitJob> recruitJobs = queryFactory.selectFrom(recruitJob)
                .where(recruitJob.project.id.in(projectIds))
                .fetch();
        return recruitJobs.stream()
                .collect(groupingBy(recruitJob -> recruitJob.getProject().getId()));
    }

    @Override
    public Map<Long, List<ProjectParticipant>> findParticipantMap(List<Long> projectIds) {
        List<ProjectParticipant> projectParticipants = queryFactory.selectFrom(projectParticipant)
                .where(projectParticipant.project.id.in(projectIds))
                .fetch();
        return projectParticipants.stream()
                .collect(groupingBy(participant -> participant.getProject().getId()));
    }

    @Override
    public Map<Long, List<ProjectLike>> findProjectLikeMap(List<Long> projectIds) {
        List<ProjectLike> projectLikes = queryFactory.selectFrom(projectLike)
                .where(projectLike.project.id.in(projectIds))
                .fetch();
        return projectLikes.stream()
                .collect(groupingBy(projectLike -> projectLike.getProject().getId()));
    }

    @Override
    public Map<Long, List<ProjectComment>> findProjectCommentMap(List<Long> projectIds) {
        List<ProjectComment> projectComments = queryFactory.selectFrom(projectComment)
                .where(projectComment.project.id.in(projectIds))
                .fetch();
        return projectComments.stream()
                .collect(groupingBy(projectComment -> projectComment.getProject().getId()));
    }

    private StringExpression buildRepresentImageUri() {
        return new CaseBuilder()
                .when(project.representImage.isNull())
                .then("/images/default?type=project")
                .otherwise(project.representImage.id.stringValue().prepend("/attachments/").concat("/images"));
    }

    private BooleanExpression subjectEq(Subject subject) {
        return subject != null ? project.subject.eq(subject) : null;
    }

    private BooleanExpression jobEq(Job job, ProjectStatus status) {
        if (job != null && ProjectStatus.RECRUITING.equals(status)) {
            // 모집중인 직무 -> Job eq && 참여자 수 < 모집 인원
            return select(projectParticipant.count())
                    .from(projectParticipant)
                    .where(projectParticipant.job.eq(job),
                            projectParticipant.project.eq(project))
                    .lt(select(recruitJob.recruitAmount.longValue())
                            .from(recruitJob)
                            .where(recruitJob.job.eq(job),
                                    recruitJob.project.eq(project)));
        } else if (job != null && status == null) {
            // 전체 직무
            return project.recruitJobs.any().job.eq(job);
        }
        return null;
    }

    private BooleanExpression skillEq(Skill skill) {
        return skill != null ? project.projectSkills.any().skill.eq(skill) : null;
    }

    private BooleanExpression statusEq(ProjectStatus status) {
        return status != null ? project.status.eq(status) : null;
    }

    private BooleanExpression profitableEq(Boolean profitable) {
        return profitable != null ? project.profitable.eq(profitable) : null;
    }

    private BooleanExpression titleLike(String keyword) {
        return StringUtils.hasText(keyword) ? project.title.contains(keyword) : null;
    }

    private BooleanExpression myLikeExists(ProjectSearchCondition searchCondition) {
        return selectFrom(projectLike)
                .where(projectLike.project.eq(project),
                        projectLike.member.id.eq(Objects.requireNonNullElse(searchCondition.getLonginMemberId(), Long.MIN_VALUE)))
                .exists();
    }
}