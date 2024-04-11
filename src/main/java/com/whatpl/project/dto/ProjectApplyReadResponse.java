package com.whatpl.project.dto;

import com.whatpl.global.common.domain.enums.Job;
import com.whatpl.project.domain.enums.ApplyStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class ProjectApplyReadResponse {

    private final long projectId;
    private final long applyId;
    private final long applicantId;
    private final String applicantNickname;
    private final Job job;
    private final ApplyStatus status;
    private final String content;
    private final LocalDateTime recruiterReadAt;
}
