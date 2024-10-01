package com.whatpl.domain.project.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@Builder
@RequiredArgsConstructor
public class ParticipatedProjectResponse {

    private final List<ParticipatedProject> participatedProjects;

    public static ParticipatedProjectResponse from(List<ParticipatedProject> participatedProject) {
        return new ParticipatedProjectResponse(participatedProject);
    }
}
