package com.whatpl.security.jwt;

import com.whatpl.security.domain.AccountPrincipal;
import com.whatpl.security.domain.OAuth2UserInfo;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    private static final String MOCK_JWT_SECRET = "UWVIUjhtcW15ZGRQOWRsdzdnMExIb0VmSFlOUTJLWTdwc0Z5WFoyNzZGUQ";

    @Mock
    JwtProperties jwtProperties;

    @InjectMocks
    JwtService jwtService;

    @Test
    @DisplayName("accessToken 을 발급한다.")
    void createAccessToken() {
        // given
        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfo.builder()
                .name("testuser")
                .build();
        AccountPrincipal principal = new AccountPrincipal(1L, "testuser", "", Collections.emptySet(), oAuth2UserInfo);
        OAuth2AuthenticationToken oAuth2AuthenticationToken = new OAuth2AuthenticationToken(principal, null, "test");

        Mockito.when(jwtProperties.getAccessExpirationTime())
                .thenReturn(60_000L);
        Mockito.when(jwtProperties.getSecretKey())
                .thenReturn(Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(MOCK_JWT_SECRET)));

        // when
        String accessToken = jwtService.createAccessToken(oAuth2AuthenticationToken);

        // then
        long countComma = accessToken.chars().filter(c -> c == '.').count();
        assertEquals(2, countComma);
    }

    @Test
    @DisplayName("refreshToken 을 발급한다.")
    void createRefreshToken() {
        // given
        long id = 1L;

        // when
        String refreshToken = jwtService.createRefreshToken(id);

        // then
        long countHyphen = refreshToken.chars().filter(c -> c == '-').count();
        assertEquals(4, countHyphen);
    }

}