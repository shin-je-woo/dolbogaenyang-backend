package com.whatpl.domain.project.dto;

import com.whatpl.global.common.model.Job;
import com.whatpl.global.common.model.Subject;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@RequiredArgsConstructor
public class ParticipatedProject {

    private final Long projectId;
    private final String title;
    private final String representImageUrl;
    private final Subject subject;
    private final Job job;
    private final LocalDateTime participatedAt;
}
