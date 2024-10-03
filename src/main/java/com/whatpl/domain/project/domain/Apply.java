package com.whatpl.domain.project.domain;

import com.whatpl.domain.chat.domain.ChatRoom;
import com.whatpl.global.common.model.BaseTimeEntity;
import com.whatpl.global.common.model.Job;
import com.whatpl.global.exception.BizException;
import com.whatpl.global.exception.ErrorCode;
import com.whatpl.domain.member.domain.Member;
import com.whatpl.global.common.model.ApplyStatus;
import com.whatpl.domain.project.model.ApplyType;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Entity
@Table(name = "apply")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Apply extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private Job job;

    @Enumerated(EnumType.STRING)
    private ApplyStatus status;

    @Enumerated(EnumType.STRING)
    private ApplyType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "applicant_id")
    private Member applicant;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    @OneToOne(mappedBy = "apply", cascade = CascadeType.PERSIST)
    private ChatRoom chatRoom;

    @Builder
    public Apply(Job job, ApplyStatus status, ApplyType type, Member applicant, Project project, ChatRoom chatRoom) {
        this.job = job;
        this.status = status;
        this.type = type;
        this.applicant = applicant;
        this.project = project;
        this.chatRoom = chatRoom;
    }

    public void addRelation(@NonNull Project project) {
        this.project = project;
    }

    public void addRelation(@NonNull ChatRoom chatRoom) {
        this.chatRoom = chatRoom;
    }

    public static Apply create(Job job, ApplyType type, Member applicant, Project project) {
        Apply apply = Apply.builder()
                .job(job)
                .status(ApplyStatus.WAITING)
                .type(type)
                .applicant(applicant)
                .project(project)
                .build();
        apply.addRelation(ChatRoom.from(apply));
        return apply;
    }

    //==비즈니스 로직==//
    public void changeStatus(ApplyStatus status) {
        if (!ApplyStatus.WAITING.equals(getStatus()) && !ApplyStatus.EXCLUDED.equals(status)) {
            // 이미 처리된 지원서는 수정 불가
            throw new BizException(ErrorCode.ALREADY_PROCESSED_APPLY);
        }
        this.status = status;
    }

    /**
     * 지원 요청을 처리합니다.
     * 지원상태를 변경하고, 지원자를 참여시킬지 결정합니다.
     */
    public void processApply(final Project project, final ApplyStatus applyStatus) {
        changeStatus(applyStatus);
        if (applyStatus.equals(ApplyStatus.ACCEPTED)) {
            participate(project, applicant);
        }
    }

    /**
     * 지원자를 프로젝트에 참여시킵니다.
     */
    private void participate(Project project, Member participant) {
        project.getRecruitJobs().stream()
                .filter(recruitJob -> recruitJob.getJob().equals(this.job))
                .findFirst()
                .ifPresent(RecruitJob::validateFullJob);
        ProjectParticipant projectParticipant = ProjectParticipant.builder()
                .project(project)
                .participant(participant)
                .job(job)
                .build();
        project.addProjectParticipant(projectParticipant);
    }

    /**
     * 중복 지원인지 검증합니다.
     */
    public void validateDuplicatedApply(Member applicant) {
        if (this.applicant.equals(applicant)) {
            throw new BizException(ErrorCode.DUPLICATED_APPLY);
        }
    }
}