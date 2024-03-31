package com.whatpl;

import com.whatpl.global.security.domain.MemberPrincipal;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.Collections;

public class WithMockWhatplMemberSecurityContextFactory implements WithSecurityContextFactory<WithMockWhatplMember> {

    @Override
    public SecurityContext createSecurityContext(WithMockWhatplMember whatplMember) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        MemberPrincipal principal = new MemberPrincipal(whatplMember.id(), "왓플테스트유저", "", Collections.emptySet());
        Authentication auth = new UsernamePasswordAuthenticationToken(principal, principal.getPassword(), principal.getAuthorities());
        context.setAuthentication(auth);
        return context;
    }
}
