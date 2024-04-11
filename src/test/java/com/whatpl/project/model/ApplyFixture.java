package com.whatpl.project.model;

import com.whatpl.global.common.domain.enums.Job;
import com.whatpl.member.domain.Member;
import com.whatpl.project.domain.Apply;
import com.whatpl.project.domain.Project;
import com.whatpl.project.domain.enums.ApplyStatus;

public class ApplyFixture {

    public static Apply waiting(Job job, Member applicant, Project project) {
        return Apply.builder()
                .job(job)
                .content("test content")
                .status(ApplyStatus.WAITING)
                .applicant(applicant)
                .project(project)
                .build();
    }

    public static Apply accepted(Job job, Member applicant, Project project) {
        return Apply.builder()
                .job(job)
                .content("test content")
                .status(ApplyStatus.ACCEPTED)
                .applicant(applicant)
                .project(project)
                .build();
    }
}
