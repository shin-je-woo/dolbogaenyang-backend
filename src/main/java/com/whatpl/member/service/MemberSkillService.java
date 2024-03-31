package com.whatpl.member.service;

import com.whatpl.global.exception.BizException;
import com.whatpl.global.exception.ErrorCode;
import com.whatpl.member.domain.Member;
import com.whatpl.member.domain.MemberSkill;
import com.whatpl.member.domain.Skill;
import com.whatpl.member.dto.MemberSkillDto;
import com.whatpl.member.repository.MemberRepository;
import com.whatpl.member.repository.MemberSkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemberSkillService {

    private final MemberRepository memberRepository;
    private final MemberSkillRepository memberSkillRepository;

    @Transactional
    public MemberSkillDto updateMemberSkills(Set<Skill> skills, long memberId) {
        // memberId에 매핑되는 memberSkill 데이터를 삭제한다. (벌크연산)
        memberSkillRepository.deleteByMemberId(memberId);

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BizException(ErrorCode.NOT_FOUND_MEMBER));

        Set<MemberSkill> memberSkills = skills.stream()
                .map(skill -> new MemberSkill(skill, member))
                .collect(Collectors.toSet());

        // memberSkills 전체 저장한다.
        memberSkillRepository.saveAll(memberSkills);
        return new MemberSkillDto(member, memberSkills);
    }
}
