package com.whatpl.domain.member.dto;

import com.whatpl.global.common.model.*;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Set;

@Getter
@Builder
@RequiredArgsConstructor
public class MemberProfileResponse {

    private final String nickname;
    private final Job job;
    private final Career career;
    private final WorkTime workTime;
    private final Boolean profileOpen;
    private final Set<Skill> skills;
    private final Set<Subject> subjects;
    private final Set<String> references;
    private final Set<Long> portfolioIds;
}
