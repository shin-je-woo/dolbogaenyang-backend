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
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.*;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
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
                .sessionManagement(AbstractHttpConfigurer::disable)
                .exceptionHandling(handler -> handler
                        .authenticationEntryPoint(new NoAuthenticationHandler(objectMapper))
                        .accessDeniedHandler(new NoAuthorizationHandler(objectMapper)));

        return http.getOrBuild();
    }

    private SecurityContextRepository securityContextRepository() {
        var httpSessionSecurityContextRepository = new HttpSessionSecurityContextRepository();
        var requestAttributeSecurityContextRepository = new RequestAttributeSecurityContextRepository();
        return new DelegatingSecurityContextRepository(httpSessionSecurityContextRepository,
                requestAttributeSecurityContextRepository);
    }
}
