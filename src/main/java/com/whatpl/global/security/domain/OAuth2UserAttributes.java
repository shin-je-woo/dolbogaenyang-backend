package com.whatpl.global.security.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;

@Getter
@RequiredArgsConstructor
public enum OAuth2UserAttributes {

    GOOGLE(attributes -> new OAuth2UserInfo(
            attributes,
            "google",
            attributes.get("sub").toString(),
            attributes.get("email").toString(),
            attributes.get("name").toString()
    )),

    NAVER(attributes -> {
        @SuppressWarnings("unchecked") // 네이버에서 보내온 api가 변경되지 않으면 타입캐스팅 안전
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");
        return new OAuth2UserInfo(
                response,
                "naver",
                response.get("id").toString(),
                response.get("email").toString(),
                response.get("name").toString()
        );
    }),

    KAKAO(attributes -> {
        @SuppressWarnings("unchecked") // 카카오에서 보내온 api가 변경되지 않으면 타입캐스팅 안전
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        @SuppressWarnings("unchecked") // 카카오에서 보내온 api가 변경되지 않으면 타입캐스팅 안전
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
        return new OAuth2UserInfo(
                profile,
                "kakao",
                attributes.get("id").toString(),
                null,
                profile.get("nickname").toString()
        );
    });

    private final Function<Map<String, Object>, OAuth2UserInfo> getOAuth2UserInfo;

    public static OAuth2UserInfo of(String registrationId, Map<String, Object> attributes) {
        return Arrays.stream(values())
                .filter(provider -> provider.name().toLowerCase().equals(registrationId))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new)
                .getOAuth2UserInfo.apply(attributes);
    }
}
