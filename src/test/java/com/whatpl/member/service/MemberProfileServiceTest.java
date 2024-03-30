package com.whatpl.member.service;

import com.whatpl.member.domain.Member;
import com.whatpl.member.dto.NicknameDuplResponse;
import com.whatpl.member.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MemberProfileServiceTest {

    @InjectMocks
    MemberProfileService memberProfileService;

    @Mock
    MemberRepository memberRepository;

    @Test
    @DisplayName("닉네임 중복 - 중복일 경우")
    void nicknameDuplCheck_dupl() {
        // given
        String nickname = "신제우";
        Member member = Member.builder().nickname(nickname).build();
        when(memberRepository.findByNickname(nickname))
                .thenReturn(Optional.of(member));

        // when
        NicknameDuplResponse response = memberProfileService.nicknameDuplCheck(nickname);

        // then
        assertFalse(response.isSuccess());
    }

    @Test
    @DisplayName("닉네임 중복 - 중복이 아닐 경우")
    void nicknameDuplCheck_not_dupl() {
        // given
        String nickname = "신제우";
        when(memberRepository.findByNickname(nickname))
                .thenReturn(Optional.empty());

        // when
        NicknameDuplResponse response = memberProfileService.nicknameDuplCheck(nickname);

        // then
        assertTrue(response.isSuccess());
    }
}