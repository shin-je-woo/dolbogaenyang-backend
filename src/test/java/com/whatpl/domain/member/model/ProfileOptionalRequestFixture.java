package com.whatpl.domain.member.model;

import com.whatpl.global.common.model.Subject;
import com.whatpl.global.common.model.WorkTime;
import com.whatpl.domain.member.dto.ProfileOptionalRequest;

import java.util.Set;

public class ProfileOptionalRequestFixture {

    public static ProfileOptionalRequest create() {
        return ProfileOptionalRequest.builder()
                .subjects(Set.of(Subject.HEALTH, Subject.SOCIAL_MEDIA))
                .references(Set.of("https://github.com", "https://notefolio.net"))
                .workTime(WorkTime.THIRTY_TO_FORTY)
                .build();
    }
}
