package com.whatpl.project.model;

import com.whatpl.global.common.domain.enums.Job;
import com.whatpl.project.domain.RecruitJob;

public class RecruitJobFixture {

    public static RecruitJob notFull(Job job) {
        return RecruitJob.builder()
                .job(job)
                .totalAmount(10)
                .currentAmount(0)
                .build();
    }

    public static RecruitJob full(Job job) {
        return RecruitJob.builder()
                .job(job)
                .totalAmount(10)
                .currentAmount(10)
                .build();
    }
}