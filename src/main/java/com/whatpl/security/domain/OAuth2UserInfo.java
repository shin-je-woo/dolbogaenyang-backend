package com.whatpl.security.domain;

import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
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

    @Builder
    public OAuth2UserInfo(Map<String, Object> attributes, String registrationId, String providerId, String email, String name) {
        this.attributes = attributes;
        this.registrationId = registrationId;
        this.providerId = providerId;
        this.email = email;
        this.name = name;
    }
}
