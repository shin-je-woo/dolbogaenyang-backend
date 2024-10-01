package com.whatpl.global.security.permission.manager;

import com.whatpl.global.security.domain.MemberPrincipal;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Component
public class MemberPermissionManager implements WhatplPermissionManager {

    @Override
    @Transactional(readOnly = true)
    public boolean hasPrivilege(MemberPrincipal memberPrincipal, Long targetId, String permission) {
        return switch (permission) {
            case "UPDATE" -> hasUpdatePrivilege(memberPrincipal, targetId);
            default -> false;
        };
    }

    /**
     * 멤버 수정 권한
     * 자기 자신
     */
    private boolean hasUpdatePrivilege(MemberPrincipal memberPrincipal, Long memberId) {
        return Objects.equals(memberId, memberPrincipal.getId());
    }
}
