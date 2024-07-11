package com.whatpl.project.domain;

import com.whatpl.attachment.domain.Attachment;
import com.whatpl.global.common.BaseTimeEntity;
import com.whatpl.global.common.domain.enums.Skill;
import com.whatpl.global.common.domain.enums.Subject;
import com.whatpl.global.exception.BizException;
import com.whatpl.global.exception.ErrorCode;
import com.whatpl.member.domain.Member;
import com.whatpl.project.domain.enums.MeetingType;
import com.whatpl.project.domain.enums.ProjectStatus;
import com.whatpl.project.dto.ProjectUpdateRequest;
import com.whatpl.project.dto.RecruitJobField;
import jakarta.persistence.*;
import lombok.*;

import java.util.*;

@Getter
@Entity
@Table(name = "project")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProjectSkill> projectSkills = new ArrayList<>();

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecruitJob> recruitJobs = new ArrayList<>();

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProjectComment> projectComments = new ArrayList<>();

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProjectLike> projectLikes = new ArrayList<>();

    @OrderBy("createdAt asc")
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
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
        projectSkill.setProject(this);
    }

    public void addRecruitJob(RecruitJob recruitJob) {
        if (recruitJob == null) {
            return;
        }
        this.recruitJobs.add(recruitJob);
        recruitJob.setProject(this);
    }

    public void addProjectParticipant(ProjectParticipant projectParticipant) {
        if (projectParticipant == null) {
            return;
        }
        this.projectParticipants.add(projectParticipant);
        projectParticipant.setProject(this);
    }

    public void addRepresentImageAndWriter(Attachment representImage, Member writer) {
        this.representImage = representImage;
        this.writer = writer;
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
}
