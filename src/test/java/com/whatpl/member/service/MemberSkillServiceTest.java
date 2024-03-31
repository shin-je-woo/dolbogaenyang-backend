package com.whatpl.member.service;

import com.whatpl.member.domain.Member;
import com.whatpl.member.domain.MemberSkill;
import com.whatpl.member.domain.Skill;
import com.whatpl.member.dto.MemberSkillDto;
import com.whatpl.member.repository.MemberRepository;
import com.whatpl.member.repository.MemberSkillRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberSkillServiceTest {

    @InjectMocks
    MemberSkillService memberSkillService;

    @Mock
    MemberRepository memberRepository;

    @Mock
    MemberSkillRepository memberSkillRepository;

    @Test
    @DisplayName("MemberSkill을 입력한다.")
    void updateMemberSkills() {
        // given
        Set<Skill> skills = new HashSet<>(Arrays.asList(Skill.JAVA, Skill.PYTHON));
        Member member = mock(Member.class);
        long memberId = 1L;
        when(member.getId())
                .thenReturn(memberId);
        when(memberRepository.findById(memberId))
                .thenReturn(Optional.of(member));

        // when
        MemberSkillDto memberSkillDto = memberSkillService.updateMemberSkills(skills, member.getId());

        // then
        verify(memberSkillRepository, times(1))
                .deleteByMemberId(memberId);
        verify(memberSkillRepository, times(1))
                .saveAll(any());
        Set<MemberSkill> memberSkills = memberSkillDto.getMemberSkills();
        Set<Skill> resultSkills = memberSkills.stream()
                .map(MemberSkill::getSkill)
                .collect(Collectors.toSet());
        assertIterableEquals(skills, resultSkills);
        assertEquals(member, memberSkillDto.getMember());
    }

}