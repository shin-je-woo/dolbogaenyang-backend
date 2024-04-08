package com.whatpl.global.security.permission.manager;

import com.whatpl.global.exception.BizException;
import com.whatpl.global.exception.ErrorCode;
import com.whatpl.global.security.domain.MemberPrincipal;
import com.whatpl.project.domain.Apply;
import com.whatpl.project.repository.ApplyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ApplyPermissionManager implements WhatplPermissionManager {

    private final ApplyRepository applyRepository;

    @Override
    @Transactional(readOnly = true)
    public boolean hasPrivilege(MemberPrincipal memberPrincipal, Long targetId, String permission) {
        if (permission.equals("READ")) {
            return hasReadPrivilege(memberPrincipal, targetId);
        }
        return false;
    }

    /**
     * 프로젝트 지원서 읽기 권한
     * 지원자 or 프로젝트 등록자
     */
    private boolean hasReadPrivilege(MemberPrincipal memberPrincipal, Long applyId) {
        Apply apply = applyRepository.findByIdWithProjectAndApplicant(applyId)
                .orElseThrow(() -> new BizException(ErrorCode.NOT_FOUND_APPLY));

        boolean isApplicant = apply.getApplicant().getId().equals(memberPrincipal.getId());
        boolean isProjectWriter = apply.getProject().getWriter().getId().equals(memberPrincipal.getId());

        return isApplicant || isProjectWriter;
    }
}
