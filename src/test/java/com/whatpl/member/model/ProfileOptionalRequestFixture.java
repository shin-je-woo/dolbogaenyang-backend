package com.whatpl.member.model;

import com.whatpl.member.domain.Subject;
import com.whatpl.member.domain.WorkTime;
import com.whatpl.member.dto.ProfileOptionalRequest;

import java.util.Set;

public class ProfileOptionalRequestFixture {

    public static ProfileOptionalRequest create() {
        return ProfileOptionalRequest.builder()
                .subjects(Set.of(Subject.HEALTH, Subject.SOCIAL_MEDIA))
                .references(Set.of("https://github.com", "https://https://notefolio.net"))
                .workTime(WorkTime.THIRTY_TO_FORTY)
                .build();
    }
}
