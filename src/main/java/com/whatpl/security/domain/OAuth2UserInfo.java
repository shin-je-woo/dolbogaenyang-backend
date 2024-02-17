package com.whatpl.security.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@Getter
@RequiredArgsConstructor
public class OAuth2UserInfo {

    /** AuthorizationServer 에서 제공받은 정보 */
    private final Map<String, Object> attributes;
    /** AuthorizationServer id */
    private final String registrationId;
    /** AuthorizationServer 에서 관리중인 사용자 고유값 */
    private final String providerId;
    /** 사용자 이메일 */
    private final String email;
    /** 사용자 이름 */
    private final String name;
}
