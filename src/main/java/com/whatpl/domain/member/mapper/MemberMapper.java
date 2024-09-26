package com.whatpl.domain.member.mapper;

import com.whatpl.domain.member.domain.*;
import com.whatpl.domain.member.dto.MemberProfileResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberMapper {

    public static MemberProfileResponse toMemberProfileResponse(Member member) {
        return MemberProfileResponse.builder()
                .nickname(member.getNickname())
                .job(member.getJob())
                .career(member.getCareer())
                .profileOpen(member.getProfileOpen())
                .workTime(member.getWorkTime())
                .skills(member.getMemberSkills().stream().map(MemberSkill::getSkill).collect(Collectors.toSet()))
                .subjects(member.getMemberSubjects().stream().map(MemberSubject::getSubject).collect(Collectors.toSet()))
                .references(member.getMemberReferences().stream().map(MemberReference::getReference).collect(Collectors.toSet()))
                .portfolioIds(member.getMemberPortfolios().stream().map(MemberPortfolio::getId).collect(Collectors.toSet()))
                .build();
    }
}
