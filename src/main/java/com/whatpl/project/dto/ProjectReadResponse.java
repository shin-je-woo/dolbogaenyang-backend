package com.whatpl.project.dto;

import com.whatpl.global.common.domain.enums.Skill;
import com.whatpl.global.common.domain.enums.Subject;
import com.whatpl.project.domain.enums.MeetingType;
import com.whatpl.project.domain.enums.ProjectStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class ProjectReadResponse {

    private final long projectId;
    private final Long representImageId;
    private final String title;
    private final ProjectStatus projectStatus;
    private final Subject subject;
    private final MeetingType meetingType;
    private final long views;
    private final long likes;
    private final boolean profitable;
    private final String writerNickname;
    private final LocalDateTime createdAt;
    private final String content;
    private final List<Skill> skills;
    private final int term;
    private final List<ProjectJobParticipantDto> projectJobParticipants;
    private final boolean myLike;
}
