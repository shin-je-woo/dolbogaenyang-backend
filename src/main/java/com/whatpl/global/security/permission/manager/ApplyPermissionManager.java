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
        return switch (permission) {
            case "READ" -> hasReadPrivilege(memberPrincipal, targetId);
            case "STATUS" -> hasStatusPrivilege(memberPrincipal, targetId);
            default -> false;
        };
    }

    /**
     * 프로젝트 지원서 읽기 권한
     * 지원자 or 프로젝트 등록자
     */
    private boolean hasReadPrivilege(MemberPrincipal memberPrincipal, Long applyId) {
        Apply apply = applyRepository.findWithProjectAndApplicantById(applyId)
                .orElseThrow(() -> new BizException(ErrorCode.NOT_FOUND_APPLY));

        boolean isApplicant = apply.getApplicant().getId().equals(memberPrincipal.getId());
        boolean isProjectWriter = apply.getProject().getWriter().getId().equals(memberPrincipal.getId());

        return isApplicant || isProjectWriter;
    }

    /**
     * 프로젝트 지원서 상태 변경 권한 (지원서 수락/거절 use-case)
     * 프로젝트 등록자
     */
    private boolean hasStatusPrivilege(MemberPrincipal memberPrincipal, Long applyId) {
        Apply apply = applyRepository.findWithProjectAndApplicantById(applyId)
                .orElseThrow(() -> new BizException(ErrorCode.NOT_FOUND_APPLY));

        return apply.getProject().getWriter().getId().equals(memberPrincipal.getId());
    }
}
