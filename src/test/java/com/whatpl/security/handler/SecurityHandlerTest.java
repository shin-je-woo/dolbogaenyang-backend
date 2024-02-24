package com.whatpl.security.handler;

import com.whatpl.account.AccountService;
import com.whatpl.exception.ErrorCode;
import com.whatpl.jwt.JwtProperties;
import com.whatpl.jwt.JwtService;
import com.whatpl.security.config.SecurityConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@Import(SecurityConfig.class)
public class SecurityHandlerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    AccountService accountService;

    @MockBean
    JwtService jwtService;

    @MockBean
    JwtProperties jwtProperties;

    @Test
    @DisplayName("인증되지 않은 사용자가 인증이 필요한 uri 로 요청하면 에러 응답")
    void test() throws Exception {
        // given
        String requiredAuthenticationUri = "/unauthorized";

        // expected
        mockMvc.perform(get(requiredAuthenticationUri))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(ErrorCode.NO_AUTHENTICATION.getCode()))
                .andExpect(jsonPath("$.message").value(ErrorCode.NO_AUTHENTICATION.getMessage()));
    }
}
