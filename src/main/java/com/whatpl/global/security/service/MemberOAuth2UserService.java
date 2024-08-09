package com.whatpl.global.security.service;

import com.whatpl.global.security.domain.OAuth2UserAttributes;
import com.whatpl.global.security.domain.OAuth2UserInfo;
import com.whatpl.domain.member.service.MemberLoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;

@RequiredArgsConstructor
public class MemberOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberLoginService memberLoginService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2UserInfo oAuth2UserInfo = OAuth2UserAttributes.of(registrationId, oAuth2User.getAttributes());

        return memberLoginService.getOrCreateMember(oAuth2UserInfo);
    }
}
