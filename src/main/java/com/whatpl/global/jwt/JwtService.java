package com.whatpl.global.jwt;

import com.whatpl.account.Account;
import com.whatpl.account.AccountRepository;
import com.whatpl.global.exception.BizException;
import com.whatpl.global.exception.ErrorCode;
import com.whatpl.global.redis.RedisService;
import com.whatpl.global.security.domain.AccountPrincipal;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtProperties jwtProperties;
    private final RedisService redisService;
    private final AccountRepository accountRepository;
    private final static String PREFIX_REFRESH_TOKEN = "refreshToken:";

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
     * refreshToken 은 Redis 에 저장한다.
     *
     * @param id Account ID
     * @return 발급된 refreshToken
     */
    public String createRefreshToken(final long id) {
        String refreshToken = UUID.randomUUID().toString();
        redisService.put(PREFIX_REFRESH_TOKEN + refreshToken, id, jwtProperties.getRefreshExpirationTime());
        return refreshToken;
    }

    /**
     * 새로운 accessToken, refreshToken 을 발급한다. (RTR 방식)
     * 기존의 refreshToken 은 만료 처리한다.
     *
     * @param refreshToken 발급가능 여부를 판단할 refreshToken
     * @return 새로운 토큰객체
     * @throws BizException refreshToken 저장소(Redis)에 refreshToken 이 존재하지 않을 경우 발생
     */
    public JwtResponse reIssueToken(final String refreshToken) {
        if (!StringUtils.hasText(refreshToken)) {
            throw new BizException(ErrorCode.INVALID_TOKEN);
        }
        Long accountId = getAccountIdFromRedis(refreshToken);
        UsernamePasswordAuthenticationToken authenticationToken = createAuthenticationToken(accountId);
        String accessToken = createAccessToken(authenticationToken);
        String reIssuedRefreshToken = createRefreshToken(accountId);
        redisService.delete(PREFIX_REFRESH_TOKEN + refreshToken);

        return JwtResponse.builder()
                .accessToken(accessToken)
                .refreshToken(reIssuedRefreshToken)
                .build();
    }

    private UsernamePasswordAuthenticationToken createAuthenticationToken(Long accountId) {
        Account account = accountRepository.findById(accountId)
                        .orElseThrow(() -> new BizException(ErrorCode.NOT_FOUND_ACCOUNT));
        AccountPrincipal principal = AccountPrincipal.of(account);
        return new UsernamePasswordAuthenticationToken(principal, "", Collections.emptySet());
    }

    /**
     * 토큰을 Authentication 객체로 변환한다.
     *
     * @param jwt 서명된 jwt
     * @return JWT Payload 값을 담은 Authentication 객체
     */
    public Authentication resolveToken(String jwt) {
        Jws<Claims> claims = parseJwt(jwt);
        AccountPrincipal accountPrincipal = getAccountPrincipal(claims);
        return new UsernamePasswordAuthenticationToken(accountPrincipal, "", Collections.emptySet());
    }

    private AccountPrincipal getAccountPrincipal(Jws<Claims> claims) {
        long id = Long.parseLong(claims.getPayload().getSubject());
        String name = claims.getPayload().get("name").toString();
        return new AccountPrincipal(id, name, "", Collections.emptySet(), null);
    }

    private Jws<Claims> parseJwt(String jwt) {
        Jws<Claims> claims;
        try {
            claims = Jwts.parser().verifyWith(jwtProperties.getSecretKey()).build().parseSignedClaims(jwt);
        } catch (JwtException e) {
            log.info("jwt 파싱 중 에러발생! {} {}", e.getClass(), e.getMessage());
            throw e;
        }
        return claims;
    }

    private Long getAccountIdFromRedis(String refreshToken) {
        if (!redisService.exists(PREFIX_REFRESH_TOKEN + refreshToken))
            throw new BizException(ErrorCode.EXPIRED_TOKEN);
        return Long.parseLong(redisService.get(PREFIX_REFRESH_TOKEN + refreshToken));
    }
}
