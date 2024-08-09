package com.whatpl.domain.project.model;

import com.whatpl.global.common.domain.enums.Career;
import com.whatpl.global.common.domain.enums.Job;
import com.whatpl.global.common.domain.enums.Skill;
import com.whatpl.global.common.domain.enums.Subject;
import com.whatpl.domain.project.domain.enums.MeetingType;
import com.whatpl.domain.project.domain.enums.ProjectStatus;
import com.whatpl.domain.project.dto.ProjectJobParticipantDto;
import com.whatpl.domain.project.dto.ProjectReadResponse;

import java.time.*;
import java.util.Collections;
import java.util.List;

public class ProjectReadResponseFixture {

    public static ProjectReadResponse from(final Long projectId) {
        return ProjectReadResponse.builder()
                .projectId(projectId)
                .representImageId(1L)
                .title("테스트 프로젝트")
                .projectStatus(ProjectStatus.RECRUITING)
                .meetingType(MeetingType.ONLINE)
                .views(100)
                .likes(30)
                .profitable(false)
                .writerNickname("닉네임입니다")
                .createdAt(LocalDateTime.now(Clock.fixed(
                        Instant.parse("2024-04-12T12:35:43.00Z"),
                        ZoneId.of("Asia/Seoul"))))
                .content("<p>테스트 콘텐츠 HTML<p>")
                .subject(Subject.SOCIAL_MEDIA)
                .skills(List.of(Skill.JAVA, Skill.JAVA_SCRIPT))
                .term(10)
                .projectJobParticipants(List.of(
                        ProjectJobParticipantDto.builder()
                                .job(Job.BACKEND_DEVELOPER)
                                .recruitAmount(10)
                                .participantAmount(2)
                                .participants(List.of(
                                        ProjectJobParticipantDto.ParticipantDto.builder()
                                                .participantId(1L)
                                                .memberId(11L)
                                                .nickname("백엔드 참여자1")
                                                .job(Job.BACKEND_DEVELOPER)
                                                .career(Career.FIVE)
                                                .build(),
                                        ProjectJobParticipantDto.ParticipantDto.builder()
                                                .participantId(2L)
                                                .memberId(22L)
                                                .job(Job.BACKEND_DEVELOPER)
                                                .nickname("백엔드 참여자2")
                                                .career(Career.NONE)
                                                .build()))
                                .build(),
                        ProjectJobParticipantDto.builder()
                                .job(Job.FRONTEND_DEVELOPER)
                                .recruitAmount(2)
                                .participantAmount(0)
                                .participants(Collections.emptyList())
                                .build()
                        ))
                .myLike(true)
                .build();
    }
}
