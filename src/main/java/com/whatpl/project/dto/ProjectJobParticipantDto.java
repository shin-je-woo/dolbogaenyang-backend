package com.whatpl.project.dto;

import com.whatpl.global.common.domain.enums.Career;
import com.whatpl.global.common.domain.enums.Job;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@Builder
@RequiredArgsConstructor
public class ProjectJobParticipantDto {

    private final Job job;
    private final int recruitAmount;
    private final int participantAmount;
    private final List<ParticipantDto> participants;

    @Getter
    @Builder
    @RequiredArgsConstructor
    public static class ParticipantDto {
        private final long participantId;
        private final long memberId;
        private final String nickname;
        private final Career career;
    }
}
