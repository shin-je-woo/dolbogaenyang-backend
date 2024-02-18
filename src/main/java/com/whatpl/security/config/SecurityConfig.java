package com.whatpl.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.whatpl.account.AccountService;
import com.whatpl.security.handler.LoginSuccessHandler;
import com.whatpl.security.jwt.JwtService;
import com.whatpl.security.repository.CookieOAuth2AuthorizationRequestRepository;
import com.whatpl.security.service.AccountOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private static final String[] WEB_SECURITY_WHITE_LIST = {"/", "/login*", "oauth2*", "/error*"};

    /*
     * 일반적인 정적자원들의 보안설정 해제
     */
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AccountService accountService,
                                                   ObjectMapper objectMapper, JwtService jwtService) throws Exception {
        http.authorizeHttpRequests(auth -> auth
                        .requestMatchers(WEB_SECURITY_WHITE_LIST).permitAll()
                        .anyRequest().authenticated())
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(new AccountOAuth2UserService(accountService)))
                        .authorizationEndpoint(auth -> auth
                                .authorizationRequestRepository(new CookieOAuth2AuthorizationRequestRepository()))
                        .successHandler(new LoginSuccessHandler(objectMapper, jwtService)))
                .formLogin(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(AbstractHttpConfigurer::disable);

        return http.getOrBuild();
    }
}
