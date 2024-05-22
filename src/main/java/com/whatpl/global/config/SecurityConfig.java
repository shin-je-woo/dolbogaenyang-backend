package com.whatpl.global.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.whatpl.global.jwt.JwtProperties;
import com.whatpl.global.jwt.JwtService;
import com.whatpl.global.security.filter.JwtAuthenticationFilter;
import com.whatpl.global.security.handler.LoginFailureHandler;
import com.whatpl.global.security.handler.LoginSuccessHandler;
import com.whatpl.global.security.handler.NoAuthenticationHandler;
import com.whatpl.global.security.handler.NoAuthorizationHandler;
import com.whatpl.global.security.repository.CookieOAuth2AuthorizationRequestRepository;
import com.whatpl.global.security.service.MemberOAuth2UserService;
import com.whatpl.member.service.MemberLoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.*;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private static final String[] WEB_SECURITY_WHITE_LIST = {"/", "/login/**", "/oauth2/**", "/error/**", "/token/**", "/docs/**", "/health-check/**"};

    /*
     * 일반적인 정적자원들의 보안설정 해제
     */
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, MemberLoginService memberLoginService,
                                                   ObjectMapper objectMapper, JwtService jwtService,
                                                   JwtProperties jwtProperties) throws Exception {
        http.authorizeHttpRequests(auth -> auth
                        .requestMatchers(WEB_SECURITY_WHITE_LIST).permitAll()
                        .requestMatchers(HttpMethod.GET, "/attachments/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/projects/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/projects/search/**").permitAll()
                        .anyRequest().authenticated())
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(new MemberOAuth2UserService(memberLoginService)))
                        .authorizationEndpoint(auth -> auth
                                .authorizationRequestRepository(new CookieOAuth2AuthorizationRequestRepository()))
                        .successHandler(new LoginSuccessHandler(objectMapper, jwtService))
                        .failureHandler(new LoginFailureHandler(objectMapper)))
                .addFilterBefore(new JwtAuthenticationFilter(objectMapper, jwtService, jwtProperties, securityContextRepository()),
                        SecurityContextHolderFilter.class)
                .formLogin(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(handler -> handler
                        .authenticationEntryPoint(new NoAuthenticationHandler(objectMapper))
                        .accessDeniedHandler(new NoAuthorizationHandler(objectMapper)));

        return http.getOrBuild();
    }

    private SecurityContextRepository securityContextRepository() {
        return new RequestAttributeSecurityContextRepository();
        // Session 으로 SecurityContextRepository 사용할 경우 로그아웃해서 JWT 날려도 Session 에 Authentication 이 남기 때문에 인증 가능한 문제 -> 세션저장소 사용 X
//        var httpSessionSecurityContextRepository = new HttpSessionSecurityContextRepository();
//        var requestAttributeSecurityContextRepository = new RequestAttributeSecurityContextRepository();
//        return new DelegatingSecurityContextRepository(httpSessionSecurityContextRepository,
//                requestAttributeSecurityContextRepository);
    }
}
