package com.whatpl.project.model;

import com.whatpl.global.common.domain.enums.Job;
import com.whatpl.project.domain.enums.ApplyType;
import com.whatpl.project.dto.ProjectApplyRequest;

public class ProjectApplyRequestFixture {

    public static ProjectApplyRequest apply(Job applyJob) {
        return ProjectApplyRequest.builder()
                .applyJob(applyJob)
                .content("test content")
                .applyType(ApplyType.APPLY)
                .build();
    }

    public static ProjectApplyRequest offer() {
        return ProjectApplyRequest.builder()
                .applyJob(Job.BACKEND_DEVELOPER)
                .content("test content")
                .applyType(ApplyType.OFFER)
                .applicantId(1L)
                .build();
    }
}
