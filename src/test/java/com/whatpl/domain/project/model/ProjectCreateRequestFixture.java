package com.whatpl.domain.project.model;

import com.whatpl.global.common.model.Job;
import com.whatpl.global.common.model.Skill;
import com.whatpl.global.common.model.Subject;
import com.whatpl.domain.project.dto.ProjectCreateRequest;
import com.whatpl.domain.project.dto.RecruitJobField;

import java.util.Set;

public class ProjectCreateRequestFixture {

    private ProjectCreateRequestFixture() {
    }

    public static ProjectCreateRequest create() {
        return ProjectCreateRequest.builder()
                .title("테스트 타이틀")
                .subject(Subject.SOCIAL_MEDIA)
                .recruitJobs(Set.of(
                        new RecruitJobField(Job.BACKEND_DEVELOPER, 5),
                        new RecruitJobField(Job.DESIGNER, 3),
                        new RecruitJobField(Job.FRONTEND_DEVELOPER, 1)
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
