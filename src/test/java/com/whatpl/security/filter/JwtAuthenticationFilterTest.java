package com.whatpl.security.filter;

import com.whatpl.account.AccountService;
import com.whatpl.global.config.SecurityConfig;
import com.whatpl.global.jwt.JwtProperties;
import com.whatpl.global.jwt.JwtService;
import com.whatpl.global.security.domain.AccountPrincipal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@WebMvcTest
@Import(SecurityConfig.class)
class JwtAuthenticationFilterTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    AccountService accountService;

    @MockBean
    JwtService jwtService;

    @MockBean
    JwtProperties jwtProperties;

    @MockBean
    SecurityContextRepository securityContextRepository;

    @Test
    @DisplayName("Authorization 헤더의 토큰이 유효할 경우 사용자 인증")
    void doFilterInternal() throws Exception {
        // given: 토큰이 유효한 경우로 세팅
        String tokenType = "Bearer";
        String validToken = "validToken";
        var principal = new AccountPrincipal(1L, "test", "", Collections.emptySet(), null);
        var authenticationToken = new UsernamePasswordAuthenticationToken(principal, "", Collections.emptySet());
        when(jwtProperties.getTokenType())
                .thenReturn(tokenType);
        when(jwtService.resolveToken(any()))
                .thenReturn(authenticationToken);

        // expected
        mockMvc.perform(get("/")
                        .header(HttpHeaders.AUTHORIZATION, tokenType + validToken))
                .andExpect(SecurityMockMvcResultMatchers.authenticated())
                .andDo(print());
    }

    @DisplayName("Authorization 헤더에 값이 없거나 지정된 TokenType 으로 시작하지 않으면 사용자 인증 X")
    @ParameterizedTest
    @ValueSource(strings = {"", "Bearer", "Basic", "Basic testToken"})
    void test(String tokenType) throws Exception {
        String invalidToken = "invalidToken";
        when(jwtProperties.getTokenType())
                .thenReturn(tokenType);

        // expected
        mockMvc.perform(get("/")
                        .header(HttpHeaders.AUTHORIZATION, tokenType + invalidToken))
                .andExpect(SecurityMockMvcResultMatchers.unauthenticated())
                .andDo(print());
    }
}