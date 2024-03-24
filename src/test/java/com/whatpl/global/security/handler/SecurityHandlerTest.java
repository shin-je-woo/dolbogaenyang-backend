package com.whatpl.global.security.handler;

import com.whatpl.BaseSecurityWebMvcTest;
import com.whatpl.global.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class SecurityHandlerTest extends BaseSecurityWebMvcTest {

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
