package com.whatpl.domain.project.model;

import com.whatpl.global.common.domain.enums.Job;
import com.whatpl.domain.project.domain.RecruitJob;

public class RecruitJobFixture {

    public static RecruitJob create(Job job) {
        return RecruitJob.builder()
                .job(job)
                .recruitAmount(5)
                .build();
    }

    public static RecruitJob withRecruitAmount(Job job, int recruitAmount) {
        return RecruitJob.builder()
                .job(job)
                .recruitAmount(recruitAmount)
                .build();
    }
}