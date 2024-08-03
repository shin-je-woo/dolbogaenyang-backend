package com.whatpl.global.security.jwt;

import com.whatpl.global.authentication.MemberPrincipalFixture;
import com.whatpl.global.cache.CacheOperator;
import com.whatpl.global.exception.BizException;
import com.whatpl.global.exception.ErrorCode;
import com.whatpl.global.jwt.JwtProperties;
import com.whatpl.global.jwt.JwtResponse;
import com.whatpl.global.jwt.JwtService;
import com.whatpl.global.security.domain.MemberPrincipal;
import com.whatpl.member.domain.Member;
import com.whatpl.member.model.MemberFixture;
import com.whatpl.member.repository.MemberRepository;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    private static final String MOCK_JWT_SECRET = "UWVIUjhtcW15ZGRQOWRsdzdnMExIb0VmSFlOUTJLWTdwc0Z5WFoyNzZGUQ";

    @Mock
    MemberRepository memberRepository;

    @Mock
    JwtProperties jwtProperties;

    @Mock
    CacheOperator cacheOperator;

    @InjectMocks
    JwtService jwtService;

    @Test
    @DisplayName("accessToken 을 발급한다.")
    void createAccessToken() {
        // given
        MemberPrincipal principal = MemberPrincipalFixture.create();
        OAuth2AuthenticationToken oAuth2AuthenticationToken = new OAuth2AuthenticationToken(principal, null, "test");

        when(jwtProperties.getAccessExpirationTime())
                .thenReturn(60_000L);
        when(jwtProperties.getSecretKey())
                .thenReturn(Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(MOCK_JWT_SECRET)));

        // when
        String accessToken = jwtService.createAccessToken(oAuth2AuthenticationToken);
        Authentication authentication = jwtService.resolveToken(accessToken);

        // then
        long countComma = accessToken.chars().filter(c -> c == '.').count();
        assertEquals(2, countComma);
        MemberPrincipal resultPrincipal = (MemberPrincipal) authentication.getPrincipal();
        assertEquals(principal.getId(), resultPrincipal.getId());
        assertEquals(principal.getUsername(), resultPrincipal.getUsername());
    }

    @Test
    @DisplayName("refreshToken 을 발급한다.")
    void createRefreshToken() {
        // given
        long id = 1L;
        long refreshTokenExpirationTime = 60_000L;
        String prefix = "refreshToken:";
        when(jwtProperties.getRefreshExpirationTime())
                .thenReturn(refreshTokenExpirationTime);

        // when
        String refreshToken = jwtService.createRefreshToken(id);

        // then
        long countHyphen = refreshToken.chars().filter(c -> c == '-').count();
        assertEquals(4, countHyphen);
        verify(cacheOperator, times(1))
                .put(prefix + refreshToken, id, refreshTokenExpirationTime);
    }

    @Test
    @DisplayName("만료된 토큰은 ExpiredJwtException 발생")
    void expiredJwtException() {
        // given
        String jwt = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwibmFtZSI6InRlc3R1c2VyIiwiaXNzIjoiamV3b29zLnNpdGUiLCJleHAiOjE3MDgyNTUzODF9.dUbi45cHVDOiPRD6kprHt3sxs-VJCh40aXKbUERUtgk";
        when(jwtProperties.getSecretKey())
                .thenReturn(Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(MOCK_JWT_SECRET)));

        // expected
        assertThrows(ExpiredJwtException.class, () -> jwtService.resolveToken(jwt));
    }

    @Test
    @DisplayName("변조된 토큰은 SignatureException 발생")
    void signatureException() {
        // given
        String jwt = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWqqqxIiwibmFtZSI6InRlc3R1c2VyIiwiaXNzIjoiamV3b29zLnNpdGUiLCJleHAiOjE3MDgyNTUzODF9.dUbi45cHVDOiPRD6kprHt3sxs-VJCh40aXKbUERUtgk";
        when(jwtProperties.getSecretKey())
                .thenReturn(Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(MOCK_JWT_SECRET)));

        // expected
        assertThrows(SignatureException.class, () -> jwtService.resolveToken(jwt));
    }

    @Test
    @DisplayName("잘못된 형식의 토큰은 MalformedJwtException 발생")
    void malformedJwtException() {
        // given
        String jwt = "aaa";
        when(jwtProperties.getSecretKey())
                .thenReturn(Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(MOCK_JWT_SECRET)));

        // expected
        assertThrows(MalformedJwtException.class, () -> jwtService.resolveToken(jwt));
    }

    @Test
    @DisplayName("토큰 재발급 파라미터가 빈 값일 경우 예외 발생")
    void reIssueTokenEmptyParam() {
        // given
        String refreshToken = "";

        // expected
        BizException bizException = assertThrows(BizException.class, () ->
                jwtService.reIssueToken(refreshToken));
        assertEquals(ErrorCode.INVALID_TOKEN, bizException.getErrorCode());
    }

    @Test
    @DisplayName("Redis 에서 리프레쉬 토큰 없으면 예외 발생")
    void reIssueTokenNotFoundToken() {
        // given
        String refreshToken = "refreshToken";
        when(cacheOperator.exists(any()))
                .thenReturn(false);

        // expected
        BizException bizException = assertThrows(BizException.class, () ->
                jwtService.reIssueToken(refreshToken));
        assertEquals(ErrorCode.EXPIRED_TOKEN, bizException.getErrorCode());
    }

    @Test
    @DisplayName("리프레쉬 토큰으로 토큰 재발급")
    void reIssueToken() {
        // given
        String refreshToken = "refreshToken";
        long memberId = 1L;
        when(cacheOperator.exists(any()))
                .thenReturn(true);
        when(cacheOperator.get(any()))
                .thenReturn(String.valueOf(memberId));
        Member testMember = MemberFixture.withAll();
        when(memberRepository.findMemberWithAllById(memberId))
                .thenReturn(Optional.of(testMember));
        MemberPrincipal expectedPrincipal = MemberPrincipalFixture.create();
        MockedStatic<MemberPrincipal> memberPrincipalMock = mockStatic(MemberPrincipal.class);
        memberPrincipalMock.when(() -> MemberPrincipal.from(testMember))
                .thenReturn(expectedPrincipal);
        when(jwtProperties.getSecretKey())
                .thenReturn(Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(MOCK_JWT_SECRET)));

        // when
        JwtResponse jwtResponse = jwtService.reIssueToken(refreshToken);

        // then
        verify(cacheOperator, times(1))
                .delete(any());
        long countComma = jwtResponse.getAccessToken().chars().filter(c -> c == '.').count();
        assertEquals(2, countComma);
        long countHyphen = jwtResponse.getRefreshToken().chars().filter(c -> c == '-').count();
        assertEquals(4, countHyphen);

        memberPrincipalMock.close();
    }
}