package com.whatpl.global.security.domain;

import com.whatpl.member.domain.Member;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class MemberPrincipal extends User implements OAuth2User {

    @Getter
    private final long id;
    private final boolean hasProfile;

    public MemberPrincipal(long id, boolean hasProfile, String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
        this.id = id;
        this.hasProfile = hasProfile;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return Collections.emptyMap();
    }

    @Override
    public String getName() {
        return super.getUsername();
    }

    public boolean getHasProfile() {
        return hasProfile;
    }

    public static MemberPrincipal from(Member member) {
        return new MemberPrincipal(member.getId(), member.hasProfile(), member.getNickname(), "", Collections.emptySet());
    }
}
