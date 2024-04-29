package com.whatpl.project.model;

import com.whatpl.global.common.domain.enums.Job;
import com.whatpl.global.common.domain.enums.Skill;
import com.whatpl.global.common.domain.enums.Subject;
import com.whatpl.project.domain.enums.MeetingType;
import com.whatpl.project.dto.ProjectCreateRequest;

import java.time.LocalDate;
import java.util.Set;

public class ProjectCreateRequestFixture {

    private ProjectCreateRequestFixture() {
    }

    public static ProjectCreateRequest create() {
        return ProjectCreateRequest.builder()
                .title("테스트 타이틀")
                .subjects(Set.of(Subject.SOCIAL_MEDIA, Subject.HEALTH))
                .recruitJobs(Set.of(
                        new ProjectCreateRequest.RecruitJobField(Job.BACKEND_DEVELOPER, 5),
                        new ProjectCreateRequest.RecruitJobField(Job.DESIGNER, 3),
                        new ProjectCreateRequest.RecruitJobField(Job.DATA_SCIENTIST, 1)
                ))
                .skills(Set.of(Skill.JAVA, Skill.FIGMA, Skill.PYTHON))
                .content("<p>테스트 콘텐츠 HTML<p>")
                .profitable(false)
                .meetingType(MeetingType.ONLINE)
                .startDate(LocalDate.of(2024, 4, 1))
                .endDate(LocalDate.of(2024, 6, 30))
                .representImageId(1L)
                .build();
    }
}
