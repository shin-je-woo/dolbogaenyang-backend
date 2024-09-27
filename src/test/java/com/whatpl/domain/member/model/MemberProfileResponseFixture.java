package com.whatpl.domain.member.model;

import com.whatpl.domain.member.dto.MemberProfileResponse;
import com.whatpl.global.common.model.*;

import java.util.Set;

public class MemberProfileResponseFixture {

    public static MemberProfileResponse generate() {
        return MemberProfileResponse.builder()
                .nickname("왓플테스트멤버1")
                .job(Job.BACKEND_DEVELOPER)
                .career(Career.SIX)
                .workTime(WorkTime.THIRTY_TO_FORTY)
                .profileOpen(true)
                .skills(Set.of(Skill.NODE, Skill.JAVA))
                .subjects(Set.of(Subject.FINANCE, Subject.HEALTH))
                .references(Set.of("https://google.com", "https://github.com"))
                .portfolioIds(Set.of(1L, 2L, 3L))
                .build();
    }
}
