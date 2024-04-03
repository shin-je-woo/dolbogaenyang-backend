package com.whatpl.member.service;

import com.whatpl.attachment.domain.Attachment;
import com.whatpl.member.domain.*;
import com.whatpl.member.dto.NicknameDuplResponse;
import com.whatpl.member.dto.ProfileOptionalRequest;
import com.whatpl.member.dto.ProfileRequiredRequest;
import com.whatpl.member.model.MemberFixture;
import com.whatpl.member.model.ProfileOptionalRequestFixture;
import com.whatpl.member.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MemberProfileServiceTest {

    @InjectMocks
    MemberProfileService memberProfileService;

    @Mock
    MemberRepository memberRepository;

    @Mock
    MemberPortfolioService memberPortfolioService;

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

    @Test
    @DisplayName("필수정보 입력 시 멤버의 필수정보가 변경된다.")
    void updateRequiredProfile() {
        // given
        Member member = MemberFixture.withAll();
        when(memberRepository.findMemberWithAllById(any(Long.class)))
                .thenReturn(Optional.of(member));
        ProfileRequiredRequest request = ProfileRequiredRequest.builder()
                .profileOpen(false)
                .job(Job.BACKEND_DEVELOPER)
                .career(Career.FIVE)
                .skills(Set.of(Skill.PYTHON, Skill.SQL))
                .build();

        // when
        memberProfileService.updateRequiredProfile(request, any(Long.class));

        // then
        assertFalse(member.getProfileOpen());
        assertEquals(Job.BACKEND_DEVELOPER, member.getJob());
        assertEquals(Career.FIVE, member.getCareer());
        assertEquals(2, member.getMemberSkills().size());
        assertTrue(member.getMemberSkills().stream()
                .map(MemberSkill::getSkill)
                .collect(Collectors.toSet())
                .containsAll(Set.of(Skill.PYTHON, Skill.SQL)));
    }

    @Test
    @DisplayName("선택정보 입력 시 멤버의 선택정보가 변경된다.")
    void updateOptionalProfile() {
        // given
        ProfileOptionalRequest request = ProfileOptionalRequestFixture.create();
        Member member = MemberFixture.onlyRequired();
        when(memberRepository.findMemberWithAllById(any(Long.class)))
                .thenReturn(Optional.of(member));
        List<MultipartFile> portfolios = List.of(
                new MockMultipartFile("testFile1", "testFile1".getBytes()),
                new MockMultipartFile("testFile2", "testFile2".getBytes()));
        when(memberPortfolioService.uploadPortfolio(portfolios))
                .thenReturn(List.of(
                        new MemberPortfolio(new Attachment("testFile1", UUID.randomUUID().toString(), "image/jpeg")),
                        new MemberPortfolio(new Attachment("testFile2", UUID.randomUUID().toString(), "image/png")))
                );

        // when
        memberProfileService.updateOptionalProfile(request, portfolios, 0L);

        // then
        assertEquals(request.getReferences().size(), member.getMemberReferences().size());
        assertEquals(request.getSubjects().size(), member.getMemberSubjects().size());
        assertEquals(request.getWorkTime(), member.getWorkTime());
        assertEquals(portfolios.size(), member.getMemberPortfolios().size());
    }
}