package com.whatpl.project.dto;

import com.whatpl.global.common.domain.enums.Job;
import com.whatpl.project.domain.enums.ApplyStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ProjectApplyReadResponse {

    private final long projectId;
    private final long applyId;
    private final long applicantId;
    private final String applicantNickname;
    private final Job job;
    private final ApplyStatus status;
    private final String content;

    @Builder
    public ProjectApplyReadResponse(long projectId, long applyId, long applicantId, String applicantNickname, Job job, ApplyStatus status, String content) {
        this.projectId = projectId;
        this.applyId = applyId;
        this.applicantId = applicantId;
        this.applicantNickname = applicantNickname;
        this.job = job;
        this.status = status;
        this.content = content;
    }
}
