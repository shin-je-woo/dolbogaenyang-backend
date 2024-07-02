package com.whatpl.project.dto;

import com.whatpl.global.common.domain.enums.Career;
import com.whatpl.global.common.domain.enums.Job;
import com.whatpl.project.domain.ProjectParticipant;
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
        private final Job job;
        private final String nickname;
        private final Career career;

        public static ParticipantDto from(ProjectParticipant projectParticipant) {
            return ProjectJobParticipantDto.ParticipantDto.builder()
                    .participantId(projectParticipant.getId())
                    .memberId(projectParticipant.getParticipant().getId())
                    .job(projectParticipant.getParticipant().getJob())
                    .nickname(projectParticipant.getParticipant().getNickname())
                    .career(projectParticipant.getParticipant().getCareer())
                    .build();
        }
    }
}
