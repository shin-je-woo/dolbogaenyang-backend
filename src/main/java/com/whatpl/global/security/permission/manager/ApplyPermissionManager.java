package com.whatpl.global.security.permission.manager;

import com.whatpl.global.exception.BizException;
import com.whatpl.global.exception.ErrorCode;
import com.whatpl.global.security.domain.MemberPrincipal;
import com.whatpl.project.domain.Apply;
import com.whatpl.project.domain.Project;
import com.whatpl.project.repository.ApplyRepository;
import com.whatpl.project.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ApplyPermissionManager implements WhatplPermissionManager {

    private final ProjectRepository projectRepository;
    private final ApplyRepository applyRepository;

    @Override
    @Transactional(readOnly = true)
    public boolean hasPrivilege(MemberPrincipal memberPrincipal, Long targetId, String permission) {
        return switch (permission) {
            case "APPLY" -> hasApplyPrivilege(memberPrincipal);
            case "OFFER" -> hasOfferPrivilege(memberPrincipal, targetId);
            case "STATUS" -> hasStatusPrivilege(memberPrincipal, targetId);
            default -> false;
        };
    }

    /**
     * 프로젝트 지원 권한
     * 인증된 사용자
     */
    private boolean hasApplyPrivilege(MemberPrincipal memberPrincipal) {
        return memberPrincipal != null;
    }

    /**
     * 프로젝트 참여 제안 권한
     * 프로젝트 등록자
     */
    private boolean hasOfferPrivilege(MemberPrincipal memberPrincipal, Long projectId) {
        Project project = projectRepository.findWithWriterById(projectId)
                .orElseThrow(() -> new BizException(ErrorCode.NOT_FOUND_PROJECT));
        long writerId = project.getWriter().getId();
        return writerId == memberPrincipal.getId();
    }

    /**
     * 프로젝트 지원 상태 변경 권한 (지원 수락/거절 use-case)
     * 프로젝트 등록자
     */
    private boolean hasStatusPrivilege(MemberPrincipal memberPrincipal, Long applyId) {
        Apply apply = applyRepository.findWithProjectAndApplicantById(applyId)
                .orElseThrow(() -> new BizException(ErrorCode.NOT_FOUND_APPLY));

        return apply.getProject().getWriter().getId().equals(memberPrincipal.getId());
    }
}
