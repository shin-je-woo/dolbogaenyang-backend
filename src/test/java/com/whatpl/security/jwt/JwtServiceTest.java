package com.whatpl.security.jwt;

import com.whatpl.redis.RedisService;
import com.whatpl.security.domain.AccountPrincipal;
import com.whatpl.security.domain.OAuth2UserInfo;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    private static final String MOCK_JWT_SECRET = "UWVIUjhtcW15ZGRQOWRsdzdnMExIb0VmSFlOUTJLWTdwc0Z5WFoyNzZGUQ";

    @Mock
    JwtProperties jwtProperties;

    @Mock
    RedisService redisService;

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
        Authentication authentication = jwtService.resolveToken(accessToken);

        // then
        long countComma = accessToken.chars().filter(c -> c == '.').count();
        assertEquals(2, countComma);
        AccountPrincipal resultPrincipal = (AccountPrincipal) authentication.getPrincipal();
        assertEquals(principal.getId(), resultPrincipal.getId());
        assertEquals(principal.getUsername(), resultPrincipal.getUsername());
        assertNull(resultPrincipal.getOAuth2UserInfo());
    }

    @Test
    @DisplayName("refreshToken 을 발급한다.")
    void createRefreshToken() {
        // given
        long id = 1L;
        long refreshTokenExpirationTime = 60_000L;
        String prefix = "refreshToken:";
        Mockito.when(jwtProperties.getRefreshExpirationTime())
                .thenReturn(refreshTokenExpirationTime);

        // when
        String refreshToken = jwtService.createRefreshToken(id);

        // then
        long countHyphen = refreshToken.chars().filter(c -> c == '-').count();
        assertEquals(4, countHyphen);
        Mockito.verify(redisService, Mockito.times(1))
                .put(prefix + refreshToken, id, refreshTokenExpirationTime);
    }

    @Test
    @DisplayName("만료된 토큰은 ExpiredJwtException 발생")
    void expiredJwtException() {
        // given
        String jwt = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwibmFtZSI6InRlc3R1c2VyIiwiaXNzIjoiamV3b29zLnNpdGUiLCJleHAiOjE3MDgyNTUzODF9.dUbi45cHVDOiPRD6kprHt3sxs-VJCh40aXKbUERUtgk";
        Mockito.when(jwtProperties.getSecretKey())
                .thenReturn(Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(MOCK_JWT_SECRET)));

        // expected
        assertThrows(ExpiredJwtException.class, () -> jwtService.resolveToken(jwt));
    }

    @Test
    @DisplayName("변조된 토큰은 SignatureException 발생")
    void signatureException() {
        // given
        String jwt = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWqqqxIiwibmFtZSI6InRlc3R1c2VyIiwiaXNzIjoiamV3b29zLnNpdGUiLCJleHAiOjE3MDgyNTUzODF9.dUbi45cHVDOiPRD6kprHt3sxs-VJCh40aXKbUERUtgk";
        Mockito.when(jwtProperties.getSecretKey())
                .thenReturn(Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(MOCK_JWT_SECRET)));

        // expected
        assertThrows(SignatureException.class, () -> jwtService.resolveToken(jwt));
    }
}