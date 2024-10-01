package com.whatpl.domain.member.dto;

import com.whatpl.global.common.model.*;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@Builder
@RequiredArgsConstructor
public class MemberProfileResponse {

    private final String nickname;
    private final Job job;
    private final Career career;
    private final WorkTime workTime;
    private final Boolean profileOpen;
    private final List<Skill> skills;
    private final List<Subject> subjects;
    private final List<String> references;
    private final List<String> portfolioUrls;
    private final String pictureUrl;
}
