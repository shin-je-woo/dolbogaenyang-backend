package com.whatpl.domain.project.dto;

import com.whatpl.global.common.model.Skill;
import com.whatpl.global.common.model.Subject;
import com.whatpl.domain.project.model.MeetingType;
import com.whatpl.domain.project.model.ProjectStatus;
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
    private final String representImageUrl;
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
