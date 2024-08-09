package com.whatpl.domain.project.model;

import com.whatpl.global.common.model.Job;
import com.whatpl.global.common.model.Skill;
import com.whatpl.global.common.model.Subject;
import com.whatpl.domain.project.dto.ProjectUpdateRequest;
import com.whatpl.domain.project.dto.RecruitJobField;

import java.util.Set;

public class ProjectUpdateRequestFixture {

    private ProjectUpdateRequestFixture() {
    }

    public static ProjectUpdateRequest create() {
        return ProjectUpdateRequest.builder()
                .title("수정 타이틀")
                .subject(Subject.EDUCATION)
                .recruitJobs(Set.of(
                        new RecruitJobField(Job.BACKEND_DEVELOPER, 5),
                        new RecruitJobField(Job.DESIGNER, 5)
                ))
                .skills(Set.of(Skill.JAVA, Skill.PYTHON))
                .content("<p>수정 콘텐츠 HTML<p>")
                .profitable(false)
                .meetingType(MeetingType.OFFLINE)
                .term(5)
                .representImageId(2L)
                .build();
    }
}
