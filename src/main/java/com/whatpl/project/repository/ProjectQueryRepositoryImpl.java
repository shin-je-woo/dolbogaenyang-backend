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
import com.whatpl.global.util.PaginationUtils;
import com.whatpl.project.domain.*;
import com.whatpl.project.domain.enums.ProjectStatus;
import com.whatpl.project.dto.ProjectInfo;
import com.whatpl.project.dto.ProjectSearchCondition;
import com.whatpl.project.dto.RemainedJobDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
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

    @Override
    public Slice<ProjectInfo> search(Pageable pageable, ProjectSearchCondition searchCondition) {
        // Project (루트) 조회
        List<ProjectInfo> projectInfos = findProjectInfos(pageable, searchCondition);
        List<Long> projectIds = toProjectIds(projectInfos);
        // ProjectSkill (1:N) 조회
        Map<Long, List<ProjectSkill>> projectSkillMap = findProjectSkillMap(projectIds);
        // RecruitJob (1:N) 조회
        Map<Long, List<RecruitJob>> recruitJobMap = findRecruitJobMap(projectIds);
        // ProjectParticipant (1:N) 조회
        Map<Long, List<ProjectParticipant>> participantMap = findParticipantMap(projectIds);
        // ProjectLike (1:N) 조회
        Map<Long, List<ProjectLike>> projectLikeMap = findProjectLikeMap(projectIds);
        // ProjectComment (1:N) 조회
        Map<Long, List<ProjectComment>> projectCommentMap = findProjectCommentMap(projectIds);

        projectInfos.forEach(projectInfo -> {
            long projectId = projectInfo.getProjectId();
            // 기술 스택 추가
            projectInfo.setSkills(projectSkillMap.get(projectId).stream()
                    .map(ProjectSkill::getSkill).toList());
            // 모집이 완료되지 않은 직무 추가
            List<RecruitJob> recruitJobs = recruitJobMap.get(projectId);
            List<ProjectParticipant> participants = Optional.ofNullable(participantMap.get(projectId)).orElseGet(Collections::emptyList);
            List<RemainedJobDto> remainedJobs = recruitJobs.stream()
                    .map(recruitJob -> RemainedJobDto.builder()
                            .job(recruitJob.getJob())
                            .recruitAmount(recruitJob.getRecruitAmount())
                            .remainedAmount(recruitJobs.size() - participants.size())
                            .build())
                    .filter(remainedJob -> remainedJob.getRemainedAmount() != 0)
                    .toList();
            projectInfo.setRemainedJobs(remainedJobs);
            // 좋아요 갯수 추가
            projectInfo.setLikes(Optional.ofNullable(projectLikeMap.get(projectId)).orElseGet(Collections::emptyList).size());
            // 댓글 갯수 추가
            projectInfo.setComments(Optional.ofNullable(projectCommentMap.get(projectId)).orElseGet(Collections::emptyList).size());
        });

        return new SliceImpl<>(projectInfos, pageable, PaginationUtils.hasNext(projectInfos, pageable.getPageSize()));
    }

    private List<ProjectInfo> findProjectInfos(Pageable pageable, ProjectSearchCondition searchCondition) {
        return queryFactory.select(fields(ProjectInfo.class,
                        project.id.as("projectId"),
                        project.title.as("title"),
                        project.status.as("status"),
                        project.subject.as("subject"),
                        project.profitable.as("profitable"),
                        project.views.as("vies"),
                        buildRepresentImageUri(),
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

    private Map<Long, List<ProjectSkill>> findProjectSkillMap(List<Long> projectIds) {
        List<ProjectSkill> projectSkills = queryFactory.selectFrom(projectSkill)
                .where(projectSkill.project.id.in(projectIds))
                .fetch();
        return projectSkills.stream()
                .collect(groupingBy(projectSkill -> projectSkill.getProject().getId()));
    }

    private Map<Long, List<RecruitJob>> findRecruitJobMap(List<Long> projectIds) {
        List<RecruitJob> recruitJobs = queryFactory.selectFrom(recruitJob)
                .where(recruitJob.project.id.in(projectIds))
                .fetch();
        return recruitJobs.stream()
                .collect(groupingBy(recruitJob -> recruitJob.getProject().getId()));
    }

    private Map<Long, List<ProjectParticipant>> findParticipantMap(List<Long> projectIds) {
        List<ProjectParticipant> projectParticipants = queryFactory.selectFrom(projectParticipant)
                .where(projectParticipant.project.id.in(projectIds))
                .fetch();
        return projectParticipants.stream()
                .collect(groupingBy(participant -> participant.getProject().getId()));
    }

    private Map<Long, List<ProjectLike>> findProjectLikeMap(List<Long> projectIds) {
        List<ProjectLike> projectLikes = queryFactory.selectFrom(projectLike)
                .where(projectLike.project.id.in(projectIds))
                .fetch();
        return projectLikes.stream()
                .collect(groupingBy(projectLike -> projectLike.getProject().getId()));
    }

    private Map<Long, List<ProjectComment>> findProjectCommentMap(List<Long> projectIds) {
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
                .otherwise(project.representImage.id.stringValue().prepend("/attachments/").concat("/images")).as("representImageUri");
    }

    private List<Long> toProjectIds(List<ProjectInfo> projectInfos) {
        return projectInfos.stream()
                .map(ProjectInfo::getProjectId)
                .toList();
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
            project.recruitJobs.any().job.eq(job);
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