package com.whatpl.domain.member.service;

import com.whatpl.global.security.domain.MemberPrincipal;
import com.whatpl.global.security.domain.OAuth2UserInfo;
import com.whatpl.domain.member.domain.Member;
import com.whatpl.domain.member.model.MemberStatus;
import com.whatpl.domain.member.model.SocialType;
import com.whatpl.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberLoginService {

    private final MemberRepository memberRepository;

    private Member createMember(OAuth2UserInfo oAuth2UserInfo) {
        Member member = Member.builder()
                .nickname(oAuth2UserInfo.getName())
                .socialType(SocialType.valueOf(oAuth2UserInfo.getRegistrationId().toUpperCase()))
                .socialId(oAuth2UserInfo.getProviderId())
                .status(MemberStatus.ACTIVE)
                .build();
        return memberRepository.save(member);
    }

    @Transactional
    public MemberPrincipal getOrCreateMember(OAuth2UserInfo oAuth2UserInfo) {
        Member member = memberRepository.findMemberWithAllBySocialTypeId(
                        SocialType.valueOf(oAuth2UserInfo.getRegistrationId().toUpperCase()),
                        oAuth2UserInfo.getProviderId())
                .orElseGet(() -> createMember(oAuth2UserInfo));

        return MemberPrincipal.from(member);
    }
}
