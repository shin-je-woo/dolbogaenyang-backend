package com.whatpl.project.model;

import com.whatpl.global.common.domain.enums.Career;
import com.whatpl.global.common.domain.enums.Job;
import com.whatpl.global.common.domain.enums.Skill;
import com.whatpl.global.common.domain.enums.Subject;
import com.whatpl.project.domain.enums.MeetingType;
import com.whatpl.project.domain.enums.ProjectStatus;
import com.whatpl.project.dto.ProjectJobParticipantDto;
import com.whatpl.project.dto.ProjectReadResponse;

import java.time.*;
import java.util.Collections;
import java.util.List;

public class ProjectReadResponseFixture {

    public static ProjectReadResponse from(final Long projectId) {
        return ProjectReadResponse.builder()
                .projectId(projectId)
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
                .subjects(List.of(Subject.HEALTH, Subject.SOCIAL_MEDIA))
                .skills(List.of(Skill.JAVA, Skill.JAVA_SCRIPT))
                .startDate(LocalDate.of(2024, 4, 15))
                .endDate(LocalDate.of(2024, 4, 30))
                .projectJobParticipants(List.of(
                        ProjectJobParticipantDto.builder()
                                .job(Job.BACKEND_DEVELOPER)
                                .totalAmount(10)
                                .currentAmount(2)
                                .participants(List.of(
                                        ProjectJobParticipantDto.ParticipantDto.builder()
                                                .memberId(1L)
                                                .nickname("백엔드 참여자1")
                                                .career(Career.FIVE)
                                                .build(),
                                        ProjectJobParticipantDto.ParticipantDto.builder()
                                                .memberId(2L)
                                                .nickname("백엔드 참여자2")
                                                .career(Career.NONE)
                                                .build()))
                                .build(),
                        ProjectJobParticipantDto.builder()
                                .job(Job.MOBILE_DEVELOPER)
                                .totalAmount(2)
                                .currentAmount(0)
                                .participants(Collections.emptyList())
                                .build()
                        ))
                .build();
    }
}
