package com.whatpl.global.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.whatpl.global.exception.BizException;
import com.whatpl.global.exception.ErrorCode;
import com.whatpl.global.exception.ErrorResponse;
import com.whatpl.global.properties.JwtProperties;
import com.whatpl.global.jwt.service.JwtService;
import com.whatpl.global.security.domain.MemberPrincipal;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
            // 인증된 사용자가 프로필을 작성했는지 확인한다.
            checkHasProfile(request, authentication);
            // SecurityContext 에 Authentication 저장
            SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
            securityContext.setAuthentication(authentication);
            SecurityContextHolder.setContext(securityContext);
            // SecurityContextHolderFilter 에서 load 될 SecurityContext 저장
            securityContextRepository.saveContext(securityContext, request, response);
            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException e) {
            responseError(response, ErrorCode.EXPIRED_TOKEN);
        } catch (MalformedJwtException e) {
            responseError(response, ErrorCode.MALFORMED_TOKEN);
        } catch (SignatureException e) {
            responseError(response, ErrorCode.INVALID_SIGNATURE);
        } catch (BizException e) {
            responseError(response, e.getErrorCode());
        }
    }

    /**
     * 요청메시지의 Authorization 헤더에 token 값이 없을 경우, 토큰 재발급 요청일 경우, Authentication 이 존재할 경우 필터 적용 하지 않는다.
     */
    @Override
    protected boolean shouldNotFilter(@Nonnull HttpServletRequest request) throws ServletException {
        return isReIssueTokenRequest(request) || !StringUtils.hasText(extractToken(request)) || SecurityContextHolder.getContext().getAuthentication() != null;
    }

    /**
     * 인증된 사용자의 프로필 작성 여부 확인
     * 프로필 필수 정보 입력 요청일 경우 skip
     */
    private void checkHasProfile(HttpServletRequest request, Authentication authentication) {
        if (isRequiredProfileRequest(request) || authentication == null) {
            return;
        }
        MemberPrincipal principal = (MemberPrincipal) authentication.getPrincipal();
        if (!principal.getHasProfile()) {
            throw new BizException(ErrorCode.HAS_NO_PROFILE);
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

    private boolean isReIssueTokenRequest(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        String method = request.getMethod();
        return ("/token".equals(requestURI) &&
                HttpMethod.POST.name().equals(method));
    }

    private boolean isRequiredProfileRequest(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        String method = request.getMethod();
        return ("/members/required".equals(requestURI) &&
                HttpMethod.POST.name().equals(method));
    }

    private void responseError(HttpServletResponse response, ErrorCode errorCode) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.displayName());
        ErrorResponse errorResponse = ErrorResponse.of(errorCode);
        objectMapper.writeValue(response.getWriter(), errorResponse);
    }
}