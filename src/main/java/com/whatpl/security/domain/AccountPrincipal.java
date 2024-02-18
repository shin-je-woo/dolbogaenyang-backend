package com.whatpl.security.domain;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

@Getter
public class AccountPrincipal extends User implements OAuth2User {

    private final long id;
    private final OAuth2UserInfo oAuth2UserInfo;

    public AccountPrincipal(long id, String username, String password, Collection<? extends GrantedAuthority> authorities, OAuth2UserInfo oAuth2UserInfo) {
        super(username, password, authorities);
        this.id = id;
        this.oAuth2UserInfo = oAuth2UserInfo;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return oAuth2UserInfo.getAttributes();
    }

    @Override
    public String getName() {
        return oAuth2UserInfo.getName();
    }
}
