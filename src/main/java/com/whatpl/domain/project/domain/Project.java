package com.whatpl.domain.project.domain;

import com.whatpl.domain.attachment.domain.Attachment;
import com.whatpl.global.common.model.BaseTimeEntity;
import com.whatpl.global.common.model.Job;
import com.whatpl.global.common.model.Skill;
import com.whatpl.global.common.model.Subject;
import com.whatpl.global.exception.BizException;
import com.whatpl.global.exception.ErrorCode;
import com.whatpl.domain.member.domain.Member;
import com.whatpl.domain.project.model.MeetingType;
import com.whatpl.domain.project.model.ProjectStatus;
import com.whatpl.domain.project.dto.ProjectUpdateRequest;
import com.whatpl.domain.project.dto.RecruitJobField;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.util.*;

@Getter
@Entity
@Table(name = "project")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "update project set status = 'DELETED' where id = ?")
@Where(clause = "status != 'DELETED'")
public class Project extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private Boolean profitable;

    private Integer term;

    private Long views;

    @Setter
    @Enumerated(EnumType.STRING)
    private ProjectStatus status;

    @Enumerated(EnumType.STRING)
    private Subject subject;

    @Enumerated(EnumType.STRING)
    private MeetingType meetingType;

    @Column(columnDefinition = "TEXT")
    private String content;

    @OneToMany(mappedBy = "project", cascade = CascadeType.PERSIST)
    private List<ProjectSkill> projectSkills = new ArrayList<>();

    @OneToMany(mappedBy = "project", cascade = CascadeType.PERSIST)
    private List<RecruitJob> recruitJobs = new ArrayList<>();

    @OneToMany(mappedBy = "project", cascade = CascadeType.PERSIST)
    private List<ProjectComment> projectComments = new ArrayList<>();

    @OneToMany(mappedBy = "project", cascade = CascadeType.PERSIST)
    private List<ProjectLike> projectLikes = new ArrayList<>();

    @OneToMany(mappedBy = "project", cascade = CascadeType.PERSIST)
    private List<Apply> applies = new ArrayList<>();

    @OrderBy("createdAt asc")
    @OneToMany(mappedBy = "project", cascade = CascadeType.PERSIST)
    private List<ProjectParticipant> projectParticipants = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "represent_image_id")
    private Attachment representImage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_id")
    private Member writer;

    @Builder
    public Project(String title, Boolean profitable, Integer term,
                   Subject subject, ProjectStatus status, MeetingType meetingType,
                   String content, Attachment representImage) {
        this.title = title;
        this.profitable = profitable;
        this.term = term;
        this.status = status;
        this.subject = subject;
        this.meetingType = meetingType;
        this.content = content;
        this.representImage = representImage;
        this.views = 0L;
    }

    //==연관관계 메서드==//
    public void addProjectSkill(ProjectSkill projectSkill) {
        if (projectSkill == null) {
            return;
        }
        this.projectSkills.add(projectSkill);
        projectSkill.addRelation(this);
    }

    public void addRecruitJob(RecruitJob recruitJob) {
        if (recruitJob == null) {
            return;
        }
        this.recruitJobs.add(recruitJob);
        recruitJob.addRelation(this);
    }

    public void addProjectParticipant(ProjectParticipant projectParticipant) {
        if (projectParticipant == null) {
            return;
        }
        this.projectParticipants.add(projectParticipant);
        projectParticipant.addRelation(this);
    }

    public void addRepresentImageAndWriter(Attachment representImage, Member writer) {
        this.representImage = representImage;
        this.writer = writer;
    }

    public void addApply(Apply apply) {
        if (apply == null) {
            return;
        }
        this.applies.add(apply);
        apply.addRelation(this);
    }

    //==비즈니스 로직==//
    public void increaseViews() {
        if (this.views == null) {
            this.views = 0L;
        }
        this.views++;
    }

    /**
     * 프로젝트를 수정합니다.
     */
    public void modify(@NonNull ProjectUpdateRequest request, Attachment representImage) {
        this.title = request.getTitle();
        this.subject = request.getSubject();
        this.content = request.getContent();
        this.profitable = request.getProfitable();
        this.meetingType = request.getMeetingType();
        this.term = request.getTerm();
        this.representImage = representImage;

        clearAndAddSkills(request.getSkills());
        compareAndModifyRecruitJobs(request.getRecruitJobs());
    }

    /**
     * 모집직군(RecruitJobs) 과 프로젝트 참여자(ProjectParticipants) 를 비교하고 수정합니다.
     */
    private void compareAndModifyRecruitJobs(Collection<RecruitJobField> recruitJobFields) {
        deleteRecruitJobs(recruitJobFields);
        modifyRecruitJobAmount(recruitJobFields);
        mergeRecruitJobs(recruitJobFields);
    }

    /**
     * 모집직군을 삭제합니다.
     * 삭제하려는 모집직군에 참여자가 존재하면 삭제할 수 없습니다.
     */
    private void deleteRecruitJobs(Collection<RecruitJobField> recruitJobFields) {
        List<RecruitJob> deleteRecruitJobs = this.recruitJobs.stream()
                .filter(recruitJob -> Optional.ofNullable(recruitJobFields)
                        .orElseGet(Collections::emptyList).stream()
                        .noneMatch(recruitJobField -> recruitJobField.getJob().equals(recruitJob.getJob())))
                .toList();
        deleteRecruitJobs.forEach(recruitJob -> {
            int participantAmount = this.projectParticipants.stream()
                    .filter(participant -> participant.getJob().equals(recruitJob.getJob()))
                    .toList().size();
            if (participantAmount > 0) {
                throw new BizException(ErrorCode.CANT_DELETE_RECRUIT_JOB_EXISTS_PARTICIPANT);
            }
        });
        this.recruitJobs.removeAll(deleteRecruitJobs);
    }

    /**
     * 모집직군의 모집인원을 수정합니다.
     * 모집인원은 프로젝트 참여자 수보다 적을 수 없습니다.
     */
    private void modifyRecruitJobAmount(Collection<RecruitJobField> recruitJobFields) {
        List<RecruitJob> modifyRecruitJobs = this.recruitJobs.stream()
                .filter(recruitJob -> Optional.ofNullable(recruitJobFields)
                        .orElseGet(Collections::emptyList).stream()
                        .anyMatch(recruitJobField -> recruitJobField.getJob().equals(recruitJob.getJob())))
                .toList();

        modifyRecruitJobs.forEach(recruitJob -> Optional.ofNullable(recruitJobFields)
                .orElseGet(Collections::emptyList)
                .forEach(recruitJobField -> {
                    if (recruitJobField.getJob().equals(recruitJob.getJob())) {
                        recruitJob.changeRecruitAmount(recruitJobField.getRecruitAmount());
                    }
                })
        );

        modifyRecruitJobs.forEach(recruitJob -> {
            int participantAmount = this.projectParticipants.stream()
                    .filter(participant -> participant.getJob().equals(recruitJob.getJob()))
                    .toList().size();
            if (recruitJob.getRecruitAmount() < participantAmount) {
                throw new BizException(ErrorCode.RECRUIT_AMOUNT_CANT_LESS_THEN_PARTICIPANT_AMOUNT);
            }
        });
    }

    /**
     * 모집직군을 추가합니다.
     * 기존에 존재하지 않던 모집직군만 추가합니다.
     */
    private void mergeRecruitJobs(Collection<RecruitJobField> recruitJobFields) {
        Optional.ofNullable(recruitJobFields)
                .orElseGet(Collections::emptyList).stream()
                .filter(recruitJobField -> this.recruitJobs.stream()
                        .noneMatch(recruitJob -> recruitJob.getJob().equals(recruitJobField.getJob())))
                .map(recruitJobField -> RecruitJob.builder()
                        .job(recruitJobField.getJob())
                        .recruitAmount(recruitJobField.getRecruitAmount())
                        .build())
                .forEach(this::addRecruitJob);
    }

    /**
     * ProjectSkills 를 비우고 새로운 ProjectSkills 를 추가 합니다.
     */
    private void clearAndAddSkills(Collection<Skill> skills) {
        this.projectSkills.clear();
        Optional.ofNullable(skills)
                .orElseGet(Collections::emptyList).stream()
                .map(ProjectSkill::new)
                .forEach(this::addProjectSkill);
    }

    /**
     * 프로젝트가 지원 가능한 상태인지 검증합니다.
     */
    public void validateCanApply(final Member applicant, final Job applyJob) {
        validateDeletedProject();
        validateCompletedRecruitment();
        validateWriter(applicant);
        validateExistsRecruitJob(applyJob);
        recruitJobs.forEach(RecruitJob::validateFullJob);
        applies.forEach(apply -> apply.validateDuplicatedApply(applicant));
    }

    /**
     * 삭제된 프로젝트인지 검증합니다.
     */
    private void validateDeletedProject() {
        if (ProjectStatus.DELETED.equals(status)) {
            throw new BizException(ErrorCode.DELETED_PROJECT);
        }
    }

    /**
     * 모집완료된 프로젝트인지 검증합니다.
     */
    private void validateCompletedRecruitment() {
        if (ProjectStatus.COMPLETED.equals(status)) {
            throw new BizException(ErrorCode.COMPLETED_RECRUITMENT);
        }
    }

    /**
     * 본인이 등록한 프로젝트인지 검증합니다.
     */
    private void validateWriter(Member applicant) {
        if (writer.equals(applicant)) {
            throw new BizException(ErrorCode.WRITER_NOT_APPLY);
        }
    }

    /**
     * 모집직군에 요청한 직군이 존재하는지 검증합니다.
     */
    private void validateExistsRecruitJob(Job job) {
        boolean existsRecruitJob = recruitJobs.stream()
                .anyMatch(recruitJob -> recruitJob.isJobMatched(job));
        if (!existsRecruitJob) {
            throw new BizException(ErrorCode.NOT_MATCH_APPLY_JOB_WITH_PROJECT);
        }
    }
}
