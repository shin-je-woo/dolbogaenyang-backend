package com.whatpl.global.jwt;

import com.whatpl.BaseSecurityWebMvcTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class JwtControllerTest extends BaseSecurityWebMvcTest {

    @Test
    @DisplayName("토큰 재발급 요청")
    void reIssue() throws Exception {
        // given
        JwtResponse jwtResponse = JwtResponse.builder()
                .accessToken("accessToken")
                .refreshToken("refreshToken")
                .build();
        when(jwtService.reIssueToken(any()))
                .thenReturn(jwtResponse);

        // when
        mockMvc.perform(post("/token")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                        .content("refreshToken=Bearer testToken"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value(jwtResponse.getAccessToken()))
                .andExpect(jsonPath("$.refreshToken").value(jwtResponse.getRefreshToken()));
    }
}