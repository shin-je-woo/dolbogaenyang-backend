package com.whatpl.project.model;

import com.whatpl.member.domain.Member;
import com.whatpl.project.domain.Project;
import com.whatpl.project.domain.ProjectComment;

public class ProjectCommentFixture {

    public static ProjectComment deleted(Project project, Member writer) {
        ProjectComment projectComment = ProjectComment.builder()
                .content("테스트 댓글")
                .project(project)
                .writer(writer)
                .build();
        projectComment.delete();
        return projectComment;
    }

    public static ProjectComment normal(Project project, Member writer) {
        return ProjectComment.builder()
                .content("테스트 댓글")
                .project(project)
                .writer(writer)
                .build();
    }
}
