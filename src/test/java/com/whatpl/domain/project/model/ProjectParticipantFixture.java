package com.whatpl.domain.project.model;

import com.whatpl.global.common.model.Job;
import com.whatpl.domain.member.domain.Member;
import com.whatpl.domain.project.domain.ProjectParticipant;

public class ProjectParticipantFixture {

    public static ProjectParticipant create(Job job, Member participant) {
        return ProjectParticipant.builder()
                .job(job)
                .participant(participant)
                .build();
    }
}
