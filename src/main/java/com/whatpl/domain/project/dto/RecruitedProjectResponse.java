package com.whatpl.domain.project.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@Builder
@RequiredArgsConstructor
public class RecruitedProjectResponse {

    private final List<ProjectInfo> recruitedProjects;

    public static RecruitedProjectResponse from(final List<ProjectInfo> recruitedProjects) {
        return new RecruitedProjectResponse(recruitedProjects);
    }
}
