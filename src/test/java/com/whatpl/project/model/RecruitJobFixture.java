package com.whatpl.project.model;

import com.whatpl.global.common.domain.enums.Job;
import com.whatpl.project.domain.RecruitJob;

public class RecruitJobFixture {

    public static RecruitJob create(Job job) {
        return RecruitJob.builder()
                .job(job)
                .recruitAmount(10)
                .build();
    }
}