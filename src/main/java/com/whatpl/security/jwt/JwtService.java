package com.whatpl.security.jwt;

import com.whatpl.security.domain.AccountPrincipal;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtProperties jwtProperties;

    /**
     * accessToken 을 발급한다.
     *
     * @param authentication SpringSecurity Authentication
     * @return 서명된 jwt
     */
    public String createAccessToken(Authentication authentication) {
        AccountPrincipal principal = (AccountPrincipal) authentication.getPrincipal();

        return Jwts.builder()
                .header()
                .type("JWT")
                .and()
                .subject(String.valueOf(principal.getId()))
                .claim("name", principal.getName())
                .issuer("jewoos.site")
                .expiration(new Date(System.currentTimeMillis() + jwtProperties.getAccessExpirationTime()))
                .signWith(jwtProperties.getSecretKey())
                .compact();
    }

    /**
     * refreshToken 을 발급한다.
     *
     * @param id Account ID
     * @return 발급된 refreshToken
     */
    public String createRefreshToken(final long id) {
        String refreshToken = UUID.randomUUID().toString();
        // TODO 리프레쉬 토큰 저장
        return refreshToken;
    }
}
