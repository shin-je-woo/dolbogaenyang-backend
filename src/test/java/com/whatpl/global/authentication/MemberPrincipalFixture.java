package com.whatpl.global.authentication;

import com.whatpl.global.common.model.Career;
import com.whatpl.global.common.model.Job;
import com.whatpl.global.security.domain.MemberPrincipal;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Collections;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberPrincipalFixture {

    public static MemberPrincipal create() {
        return MemberPrincipal.memberPrincipalBuilder()
                .id(1L)
                .hasProfile(true)
                .job(Job.BACKEND_DEVELOPER)
                .career(Career.FIVE)
                .username("왓플테스트멤버")
                .password("")
                .authorities(Collections.emptySet())
                .build();
    }
}
