package com.whatpl.domain.member.model;

import com.whatpl.domain.member.dto.MemberProfileResponse;
import com.whatpl.global.common.model.*;

import java.util.List;

public class MemberProfileResponseFixture {

    public static MemberProfileResponse generate() {
        return MemberProfileResponse.builder()
                .nickname("왓플테스트멤버1")
                .job(Job.BACKEND_DEVELOPER)
                .career(Career.SIX)
                .workTime(WorkTime.THIRTY_TO_FORTY)
                .profileOpen(true)
                .skills(List.of(Skill.NODE, Skill.JAVA))
                .subjects(List.of(Subject.FINANCE, Subject.HEALTH))
                .references(List.of("https://google.com", "https://github.com"))
                .portfolioUrls(List.of(
                        "https://jewoos.site/attachments/portfolios/1",
                        "https://jewoos.site/attachments/portfolios/2"
                ))
                .pictureUrl("https://jewoos.site/attachments/picture/1")
                .build();
    }
}
