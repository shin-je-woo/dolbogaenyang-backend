package com.whatpl.domain.project.event;

import com.whatpl.domain.project.domain.Apply;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ProjectApplyEvent {

    private final Apply apply;
    private final String applyContent;

    public static ProjectApplyEvent of(final Apply apply, final String applyContent) {
        return new ProjectApplyEvent(apply, applyContent);
    }
}
