package com.whatpl.domain.project.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ProjectReadEvent {

    private final Long projectId;

    public static ProjectReadEvent from(Long projectId) {
        return new ProjectReadEvent(projectId);
    }
}
