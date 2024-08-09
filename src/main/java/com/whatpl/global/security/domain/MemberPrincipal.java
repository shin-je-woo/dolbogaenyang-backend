package com.whatpl.global.security.domain;

import com.whatpl.global.common.domain.enums.Career;
import com.whatpl.global.common.domain.enums.Job;
import com.whatpl.domain.member.domain.Member;
import lombok.Builder;
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
    private final boolean hasProfile;
    private final Job job;
    private final Career career;

    @Builder(builderMethodName = "memberPrincipalBuilder")
    public MemberPrincipal(long id, boolean hasProfile, Job job, Career career, String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
        this.id = id;
        this.hasProfile = hasProfile;
        this.job = job;
        this.career = career;
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
        return MemberPrincipal.memberPrincipalBuilder()
                .id(member.getId())
                .hasProfile(member.hasProfile())
                .job(member.getJob())
                .career(member.getCareer())
                .username(member.getNickname())
                .password("")
                .authorities(Collections.emptySet())
                .build();
    }
}
