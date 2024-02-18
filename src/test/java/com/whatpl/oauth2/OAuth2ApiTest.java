package com.whatpl.oauth2;

import com.whatpl.account.AccountService;
import com.whatpl.security.config.SecurityConfig;
import com.whatpl.security.jwt.JwtService;
import com.whatpl.util.CookieUtils;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@WebMvcTest
@Import(SecurityConfig.class)
public class OAuth2ApiTest {

    private static final String OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME = "OAUTH2_AUTH_REQUEST";

    @Autowired
    MockMvc mockMvc;

    @MockBean
    AccountService accountService;

    @MockBean
    JwtService jwtService;

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
