package com.whatpl.project.model;

import com.whatpl.global.common.domain.enums.Job;
import com.whatpl.global.common.domain.enums.Skill;
import com.whatpl.global.common.domain.enums.Subject;
import com.whatpl.project.domain.enums.MeetingType;
import com.whatpl.project.dto.ProjectCreateRequest;

import java.util.Set;

public class ProjectCreateRequestFixture {

    private ProjectCreateRequestFixture() {
    }

    public static ProjectCreateRequest create() {
        return ProjectCreateRequest.builder()
                .title("테스트 타이틀")
                .subject(Subject.SOCIAL_MEDIA)
                .recruitJobs(Set.of(
                        new ProjectCreateRequest.RecruitJobField(Job.BACKEND_DEVELOPER, 5),
                        new ProjectCreateRequest.RecruitJobField(Job.DESIGNER, 3),
                        new ProjectCreateRequest.RecruitJobField(Job.FRONTEND_DEVELOPER, 1)
                ))
                .skills(Set.of(Skill.JAVA, Skill.FIGMA, Skill.PYTHON))
                .content("<p>테스트 콘텐츠 HTML<p>")
                .profitable(false)
                .meetingType(MeetingType.ONLINE)
                .term(10)
                .representImageId(1L)
                .build();
    }
}
