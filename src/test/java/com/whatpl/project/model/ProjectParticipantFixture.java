package com.whatpl.project.model;

import com.whatpl.global.common.domain.enums.Job;
import com.whatpl.member.domain.Member;
import com.whatpl.project.domain.ProjectParticipant;

public class ProjectParticipantFixture {

    public static ProjectParticipant create(Job job, Member participant) {
        return ProjectParticipant.builder()
                .job(job)
                .participant(participant)
                .build();
    }
}
