package com.whatpl.domain.project.dto;

import com.whatpl.global.common.model.Job;
import com.whatpl.domain.project.domain.ProjectParticipant;
import com.whatpl.domain.project.domain.RecruitJob;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class RemainedJobDto {

    private final Job job;
    private final int recruitAmount;
    private final int remainedAmount;

    @Builder
    public RemainedJobDto(Job job, int recruitAmount, int remainedAmount) {
        this.job = job;
        this.recruitAmount = recruitAmount;
        this.remainedAmount = remainedAmount;
    }

    public static RemainedJobDto of(RecruitJob recruitJob, List<ProjectParticipant> participants) {
        return RemainedJobDto.builder()
                .job(recruitJob.getJob())
                .recruitAmount(recruitJob.getRecruitAmount())
                .remainedAmount(recruitJob.getRecruitAmount() - participants.stream()
                        .filter(recruitJob::isJobMatched)
                        .toList().size())
                .build();
    }
}
