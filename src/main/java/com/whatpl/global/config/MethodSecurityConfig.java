package com.whatpl.global.config;


import com.whatpl.global.security.permission.WhatplPermissionEvaluator;
import com.whatpl.global.security.permission.manager.*;
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
    private final ProjectCommentPermissionManager projectCommentPermissionManager;
    private final ProjectLikePermissionManager projectLikePermissionManager;
    private final ParticipantPermissionManager participantPermissionManager;
    private final ChatMessagePermissionManager chatMessagePermissionManager;

    @Bean
    public MethodSecurityExpressionHandler methodSecurityExpressionHandler() {
        Map<String, WhatplPermissionManager> whatplPermissionEvaluatorMap = new HashMap<>();
        whatplPermissionEvaluatorMap.put("APPLY", applyPermissionManager);
        whatplPermissionEvaluatorMap.put("PROJECT_COMMENT", projectCommentPermissionManager);
        whatplPermissionEvaluatorMap.put("PROJECT_LIKE", projectLikePermissionManager);
        whatplPermissionEvaluatorMap.put("PARTICIPANT", participantPermissionManager);
        whatplPermissionEvaluatorMap.put("CHAT_MESSAGE", chatMessagePermissionManager);

        DefaultMethodSecurityExpressionHandler expressionHandler = new DefaultMethodSecurityExpressionHandler();
        expressionHandler.setPermissionEvaluator(new WhatplPermissionEvaluator(whatplPermissionEvaluatorMap));
        return expressionHandler;
    }
}

