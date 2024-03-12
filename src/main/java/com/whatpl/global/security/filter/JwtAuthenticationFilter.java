package com.whatpl.global.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.whatpl.global.jwt.JwtProperties;
import com.whatpl.global.jwt.JwtService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper;
    private final JwtService jwtService;
    private final JwtProperties jwtProperties;
    private final SecurityContextRepository securityContextRepository;

    @Override
    protected void doFilterInternal(@Nonnull HttpServletRequest request, @Nonnull HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String jwt = extractToken(request);
        try {
            Authentication authentication = jwtService.resolveToken(jwt);
            SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
            securityContext.setAuthentication(authentication);
            SecurityContextHolder.setContext(securityContext);
            // SecurityContextHolderFilter 에서 load 될 SecurityContext 저장
            securityContextRepository.saveContext(securityContext, request, response);
            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException e) {
            responseError(response, "만료된 토큰입니다.");
        } catch (MalformedJwtException e) {
            responseError(response, "잘못된 형식의 토큰입니다.");
        } catch (SignatureException e) {
            responseError(response, "변조된 토큰입니다.");
        }
    }

    /**
     * 요청메시지의 Authorization 헤더에서 token 을 추출한다.
     */
    private String extractToken(HttpServletRequest request) {
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasText(authorizationHeader) &&
                authorizationHeader.startsWith(jwtProperties.getTokenType()) &&
                authorizationHeader.length() > jwtProperties.getTokenType().length()) {
            return authorizationHeader.substring(jwtProperties.getTokenType().length() + 1);
        }
        return null;
    }

    private void responseError(HttpServletResponse response, String errorMsg) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.displayName());
        objectMapper.writeValue(response.getWriter(), new Object() {
            @Getter
            final String message = errorMsg;
        });
    }

    /**
     * 요청메시지의 Authorization 헤더에 token 값이 없을 경우, 토큰 재발급 요청일 경우 필터 적용 하지 않는다.
     */
    @Override
    protected boolean shouldNotFilter(@Nonnull HttpServletRequest request) throws ServletException {
        return isReIssueTokenRequest(request) || !StringUtils.hasText(extractToken(request));
    }

    private boolean isReIssueTokenRequest(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        String method = request.getMethod();
        return ("/token".equals(requestURI) &&
                HttpMethod.POST.name().equals(method));
    }
}