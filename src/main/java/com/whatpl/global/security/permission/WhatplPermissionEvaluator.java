package com.whatpl.global.security.permission;

import com.whatpl.global.security.domain.MemberPrincipal;
import com.whatpl.global.security.permission.manager.WhatplPermissionManager;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;

import java.io.Serializable;
import java.util.Map;

/**
 * 글로벌 Authorization 평가 클래스입니다.
 * 해당 클래스는 메소드 시큐리티로써 AOP 방식으로 동작합니다.
 * permissionManagers는 com.whatpl.global.config.MethodSecurityConfig에서 등록합니다.
 * targetType(평가자원) 별로 권한평가를 처리할 WhatplPermissionManager를 정의해야 합니다.
 * WhatplPermissionManager의 구현체에서 실제 targetType(평가자원)에 대한 권한처리를 수행합니다.
 *
 * AOP 적용이 필요한 메서드에 @PreAuthorize 애노테이션(해당 애노테이션이 적용된 메서드가 포인트컷입니다.)을 적용하고, SpEL로 hasPermission메서드를 호출합니다.
 * ex) @PreAuthorize("hasPermission(#applyId, 'APPLY', 'READ')")
 */
public class WhatplPermissionEvaluator implements PermissionEvaluator {

    private final Map<String, WhatplPermissionManager> permissionManagers;

    public WhatplPermissionEvaluator(Map<String, WhatplPermissionManager> permissionManagers) {
        this.permissionManagers = permissionManagers;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        // 필요시 구현
        return false;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        if (authentication == null || targetType == null || !(permission instanceof String)) {
            return false;
        }
        MemberPrincipal principal = (MemberPrincipal) authentication.getPrincipal();
        WhatplPermissionManager whatplPermissionManager = permissionManagers.get(targetType.toUpperCase());

        return whatplPermissionManager.hasPrivilege(principal, (Long) targetId, permission.toString().toUpperCase());
    }
}
