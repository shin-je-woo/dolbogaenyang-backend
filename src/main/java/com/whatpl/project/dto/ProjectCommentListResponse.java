package com.whatpl.project.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@Builder
@RequiredArgsConstructor
public class ProjectCommentListResponse {

    private final long projectId;
    private final List<ProjectCommentDto> comments;
}
