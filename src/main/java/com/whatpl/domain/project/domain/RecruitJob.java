package com.whatpl.domain.project.domain;

import com.whatpl.global.common.model.BaseTimeEntity;
import com.whatpl.global.common.model.Job;
import com.whatpl.global.exception.BizException;
import com.whatpl.global.exception.ErrorCode;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Entity
@Table(name = "recruit_job")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecruitJob extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private Job job;

    private Integer recruitAmount;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    @Builder
    public RecruitJob(Job job, int recruitAmount) {
        if(recruitAmount < 1 || recruitAmount > 5) {
            throw new IllegalArgumentException("recruitAmount must be between 1 and 5");
        }
        this.job = job;
        this.recruitAmount = recruitAmount;
    }

    public void changeRecruitAmount(int recruitAmount) {
        if(recruitAmount < 1 || recruitAmount > 5) {
            throw new IllegalArgumentException("recruitAmount must be between 1 and 5");
        }
        this.recruitAmount = recruitAmount;
    }

    public boolean isJobMatched(ProjectParticipant participant) {
        return this.job.equals(participant.getJob());
    }

    public boolean isJobMatched(Job job) {
        return this.job.equals(job);
    }

    /**
     * 모집인원이 가득 찼는지 검증합니다.
     */
    public void validateFullJob() {
        long participantAmount = project.getProjectParticipants().stream()
                .filter(participant -> participant.getJob().equals(job))
                .count();
        if (recruitAmount <= participantAmount) {
            throw new BizException(ErrorCode.RECRUIT_COMPLETED_APPLY_JOB);
        }
    }
}
