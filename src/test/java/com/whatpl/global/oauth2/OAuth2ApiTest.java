package com.whatpl.global.oauth2;

import com.whatpl.BaseSecurityWebMvcTest;
import com.whatpl.global.util.CookieUtils;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class OAuth2ApiTest extends BaseSecurityWebMvcTest {

    private static final String OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME = "OAUTH2_AUTH_REQUEST";

    @Test
    @DisplayName("인가코드 요청 API 호출시 리다이렉트 되고, AuthorizationRequest 검증용 Cookie 가 발급된다.")
    void codeTest() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/oauth2/authorization/naver"))
                .andExpect(status().is3xxRedirection())
                .andExpect(cookie().exists(OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME))
                .andExpect(cookie().httpOnly(OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME, true))
                .andExpect(cookie().secure(OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME, true))
                .andExpect(cookie().sameSite(OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME, "none"))
                .andDo(print())
                .andReturn();

        Cookie cookie = mvcResult.getResponse().getCookie(OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME);
        assertNotNull(cookie);
        assertNotNull(cookie.getValue());
        assertNotEquals(0, cookie.getValue().length());

        OAuth2AuthorizationRequest authorizationRequest = CookieUtils.deserialize(cookie, OAuth2AuthorizationRequest.class);
        assertNotNull(authorizationRequest);
    }
}
