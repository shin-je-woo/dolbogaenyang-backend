package com.whatpl.project.dto;

import com.whatpl.global.common.domain.enums.Job;
import lombok.Builder;
import lombok.Getter;

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
}
