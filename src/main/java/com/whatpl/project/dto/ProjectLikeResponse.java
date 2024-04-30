package com.whatpl.project.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@RequiredArgsConstructor
public class ProjectLikeResponse {

    private final long likeId;
    private final long projectId;
    private final long memberId;
}
