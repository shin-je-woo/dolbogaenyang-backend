package com.whatpl.domain.member.domain;

import com.whatpl.global.common.model.Career;
import com.whatpl.global.common.model.Job;
import com.whatpl.global.common.model.WorkTime;
import lombok.Builder;
import lombok.Getter;

@Getter
public class MemberEditor {

    private final String nickname;
    private final Job job;
    private final Career career;
    private final boolean profileOpen;
    private final WorkTime workTime;

    @Builder

    public MemberEditor(String nickname, Job job, Career career, boolean profileOpen, WorkTime workTime) {
        this.nickname = nickname;
        this.job = job;
        this.career = career;
        this.profileOpen = profileOpen;
        this.workTime = workTime;
    }
}
