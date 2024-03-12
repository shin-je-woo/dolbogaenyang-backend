package com.whatpl.global.security.domain;

import com.whatpl.member.domain.Member;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

@Getter
public class MemberPrincipal extends User implements OAuth2User {

    private final long id;

    public MemberPrincipal(long id, String username, String password, Collection<? extends GrantedAuthority> authorities, OAuth2UserInfo oAuth2UserInfo) {
        super(username, password, authorities);
        this.id = id;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return Collections.emptyMap();
    }

    @Override
    public String getName() {
        return super.getUsername();
    }

    public static MemberPrincipal of(Member member) {
        OAuth2UserInfo userInfo = OAuth2UserInfo.builder()
                .attributes(Collections.emptyMap())
                .name(member.getNickname())
                .registrationId(member.getSocialType().name())
                .providerId(member.getSocialId())
                .build();
        return new MemberPrincipal(member.getId(), member.getNickname(), "",
                Collections.emptySet(), userInfo);
    }
}
