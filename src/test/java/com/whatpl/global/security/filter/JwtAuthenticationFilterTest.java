package com.whatpl.global.security.filter;

import com.whatpl.BaseSecurityWebMvcTest;
import com.whatpl.global.authentication.MemberPrincipalFixture;
import com.whatpl.global.security.domain.MemberPrincipal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers;

import java.util.Collections;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

class JwtAuthenticationFilterTest extends BaseSecurityWebMvcTest {

    @Test
    @DisplayName("Authorization 헤더의 토큰이 유효할 경우 사용자 인증")
    void doFilterInternal() throws Exception {
        // given: 토큰이 유효한 경우로 세팅
        String tokenType = "Bearer";
        String validToken = "validToken";
        MemberPrincipal principal = MemberPrincipalFixture.create();
        var authenticationToken = new UsernamePasswordAuthenticationToken(principal, "", Collections.emptySet());
        when(jwtService.resolveToken(any()))
                .thenReturn(authenticationToken);

        // expected
        mockMvc.perform(get("/")
                        .header(HttpHeaders.AUTHORIZATION, tokenType + " " + validToken))
                .andExpect(SecurityMockMvcResultMatchers.authenticated())
                .andDo(print());
    }

    @DisplayName("Authorization 헤더에 값이 없거나 지정된 TokenType 으로 시작하지 않으면 사용자 인증 X")
    @ParameterizedTest
    @ValueSource(strings = {"", "Bearer", "Basic", "Basic testToken"})
    void test(String tokenType) throws Exception {
        String invalidToken = "invalidToken";

        // expected
        mockMvc.perform(get("/")
                        .header(HttpHeaders.AUTHORIZATION, tokenType + " " + invalidToken))
                .andExpect(SecurityMockMvcResultMatchers.unauthenticated())
                .andDo(print());
    }
}