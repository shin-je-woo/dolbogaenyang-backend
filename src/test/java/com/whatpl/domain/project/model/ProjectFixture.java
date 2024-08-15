package com.whatpl.domain.project.model;

import com.whatpl.global.common.model.Subject;
import com.whatpl.domain.project.domain.Project;
import com.whatpl.domain.project.domain.ProjectParticipant;
import com.whatpl.domain.project.domain.RecruitJob;

import java.util.List;

public class ProjectFixture {

    public static Project create(ProjectStatus status) {
        return Project.builder()
                .title("테스트 타이틀")
                .status(status)
                .subject(Subject.SOCIAL_MEDIA)
                .content("<p>테스트 콘텐츠 HTML<p>")
                .profitable(false)
                .meetingType(MeetingType.ONLINE)
                .term(10)
                .build();
    }

    public static Project withRecruitJobs(RecruitJob... recruitJobs) {
        Project project = create(ProjectStatus.RECRUITING);
        if(recruitJobs != null) {
            for (RecruitJob recruitJob : recruitJobs) {
                project.addRecruitJob(recruitJob);
            }
        }
        return project;
    }

    public static Project withRecruitJobAndParticipant(List<RecruitJob> recruitJobs, List<ProjectParticipant> participant) {
        Project project = create(ProjectStatus.RECRUITING);
        recruitJobs.forEach(project::addRecruitJob);
        participant.forEach(project::addProjectParticipant);
        return project;
    }
}
