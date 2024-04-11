package com.whatpl.global.config;


import com.whatpl.global.security.permission.WhatplPermissionEvaluator;
import com.whatpl.global.security.permission.manager.ApplyPermissionManager;
import com.whatpl.global.security.permission.manager.WhatplPermissionManager;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class MethodSecurityConfig {

    private final ApplyPermissionManager applyPermissionManager;

    @Bean
    public MethodSecurityExpressionHandler methodSecurityExpressionHandler() {
        Map<String, WhatplPermissionManager> whatplPermissionEvaluatorMap = new HashMap<>();
        whatplPermissionEvaluatorMap.put("APPLY", applyPermissionManager);

        DefaultMethodSecurityExpressionHandler expressionHandler = new DefaultMethodSecurityExpressionHandler();
        expressionHandler.setPermissionEvaluator(new WhatplPermissionEvaluator(whatplPermissionEvaluatorMap));
        return expressionHandler;
    }
}

