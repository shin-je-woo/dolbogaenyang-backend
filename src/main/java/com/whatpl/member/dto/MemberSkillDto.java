package com.whatpl.member.dto;

import com.whatpl.member.domain.Member;
import com.whatpl.member.domain.MemberSkill;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Set;

@Getter
@RequiredArgsConstructor
public class MemberSkillDto {

    private final Member member;
    private final Set<MemberSkill> memberSkills;
}
