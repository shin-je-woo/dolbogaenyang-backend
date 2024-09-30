package com.whatpl.domain.member.model;

import com.whatpl.domain.member.dto.ProfileUpdateRequest;
import com.whatpl.global.common.model.*;

import java.util.List;
import java.util.Set;

public class ProfileUpdateRequestFixture {

    public static ProfileUpdateRequest create() {
        return ProfileUpdateRequest.builder()
                .nickname("닉네임")
                .job(Job.BACKEND_DEVELOPER)
                .career(Career.SIX)
                .skills(Set.of(Skill.NODE, Skill.FIREBASE))
                .profileOpen(true)
                .subjects(Set.of(Subject.HEALTH, Subject.SOCIAL_MEDIA))
                .references(Set.of("https://github.com", "https://notefolio.net"))
                .workTime(WorkTime.THIRTY_TO_FORTY)
                .deletePortfolioIds(List.of(1L, 2L))
                .build();
    }
}
