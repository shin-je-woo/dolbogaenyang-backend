package com.whatpl.project.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@RequiredArgsConstructor
public class ApplyResponse {
    private final long applyId;
    private final long projectId;
    private final long chatRoomId;
}
