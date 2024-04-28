package com.whatpl.global.authentication.docs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.whatpl.ApiDocTag;
import com.whatpl.BaseSecurityWebMvcTest;
import com.whatpl.global.jwt.JwtResponse;
import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.resourceDetails;
import static org.springframework.restdocs.cookies.CookieDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
class SocialLoginDocsTest extends BaseSecurityWebMvcTest {

    private static final String OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME = "OAUTH2_AUTH_REQUEST";

    @Autowired
    SecurityFilterChain securityFilterChain;

    static class MockLoginFilter extends OncePerRequestFilter {
        @Override
        protected void doFilterInternal(@Nonnull HttpServletRequest request, @Nonnull HttpServletResponse response, @Nonnull FilterChain filterChain) throws ServletException, IOException {
            JwtResponse jwtResponse = JwtResponse.builder()
                    .accessToken("member's accessToken")
                    .refreshToken("member's refreshToken")
                    .build();
            response.setStatus(HttpStatus.OK.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding(StandardCharsets.UTF_8.displayName());
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.writeValue(response.getWriter(), jwtResponse);
        }
    }

    @Test
    @DisplayName("OAuth2 로그인 페이지 리다이렉트 요청 API Docs")
    void oauth2_redirect() throws Exception {
        // expected
        mockMvc.perform(get("/oauth2/authorization/{registrationId}", "naver"))
                .andExpectAll(
                        status().is3xxRedirection(),
                        cookie().exists(OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME),
                        cookie().httpOnly(OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME, true),
                        cookie().secure(OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME, true),
                        cookie().sameSite(OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME, "none")
                )
                .andDo(print())
                .andDo(document("redirect-oauth2-login-page",
                        resourceDetails().tag(ApiDocTag.AUTHENTICATION.getTag())
                                .summary("[소셜 로그인] 소셜 로그인 페이지로 redirect")
                                .description("""
                                        OAuth2 Authorization Server 로그인 페이지로 리다이렉트 시키는 API
                                                                                
                                        구현된 OAuth2 로그인 제공자로는 네이버(naver), 구글(google), 카카오(kakao)가 있습니다.
                                                                                
                                        요청 uri의 registrationId에 제공자를 포함시켜 요청합니다.
                                                                                
                                        요청이 성공하면 OAUTH2_AUTH_REQUEST 를 키로 갖는 쿠키가 발급 됩니다.
                                                                                
                                        해당 쿠키는 로그인 성공 후 콜백 경로에서 GET /login/oauth2/code/{registrationId} API를 호출할 때 포함시켜 요청해야 합니다.
                                        """),
                        pathParameters(
                                parameterWithName("registrationId").description("소셜 로그인 제공자 ID - [naver, google, kakao]")
                        ),
                        responseCookies(
                                cookieWithName(OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME).description("인증용 쿠키 (OAuth2AuthorizationRequest 객체가 직렬화된 값을 가짐)")
                        )
                ));
    }

    @Test
    @DisplayName("OAuth2 로그인 후 토큰 발급 API Docs")
    void oauth2_user_info() throws Exception {
        // given
        MockLoginFilter mockLoginFilter = new MockLoginFilter();
        securityFilterChain.getFilters().add(0, mockLoginFilter);
        MultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("code", "T7I7z1NCLKQ7OOs");
        requestParams.add("state", "bKYBrWA4NwA3EWNu6TRGzbBMkay0EuIQ=");
        String registrationId = "naver";

        // expected
        mockMvc.perform(get("/login/oauth2/code/{registrationId}", registrationId).params(requestParams)
                        .cookie(new Cookie(OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME, "OAuth2AuthorizationRequest")))
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE),
                        jsonPath("$.accessToken").exists(),
                        jsonPath("$.refreshToken").exists()
                )
                .andDo(print())
                .andDo(document("get-access-and-refresh-token",
                        resourceDetails().tag(ApiDocTag.AUTHENTICATION.getTag())
                                .summary("[소셜 로그인] 억세스 토큰, 리프레쉬 토큰을 요청합니다.")
                                .description("""
                                        GET /oauth2/authorization/{registrationId} API 이후 실행해야 하는 API입니다.
                                                                                
                                        Authorization Server로부터 redirect된 쿼리 파라미터 (code, state..)를 원본 그대로 쿼리파라미터로 요청합니다.
                                                                                
                                        해당 요청에는 이전 API 수행 후 발급된 쿠키(OAUTH2_AUTH_REQUEST)를 포함시켜야 합니다.
                                        """),
                        pathParameters(
                                parameterWithName("registrationId").description("소셜 로그인 제공자 ID - [naver, google, kakao]")
                        ),
                        requestCookies(
                                cookieWithName(OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME)
                                        .description("OAuth2AuthorizationRequest 객체가 직렬화된 쿠키 (스프링 시큐리티 소셜 로그인인증 수행 시 필요)")
                        ),
                        responseFields(
                                fieldWithPath("accessToken").type(JsonFieldType.STRING).description("accessToken - JWT"),
                                fieldWithPath("refreshToken").type(JsonFieldType.STRING).description("refreshToken - 임의의 값 (UUID)")
                        )
                ));

        securityFilterChain.getFilters().remove(mockLoginFilter);
    }
}
