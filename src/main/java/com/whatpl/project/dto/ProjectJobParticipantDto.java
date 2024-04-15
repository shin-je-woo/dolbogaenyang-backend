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
    private final int totalAmount;
    private final int currentAmount;
    private final List<Participant> participants;

    @Getter
    @Builder
    @RequiredArgsConstructor
    public static class Participant {
        private final Long memberId;
        private final String nickname;
        private final Career career;
    }
}
