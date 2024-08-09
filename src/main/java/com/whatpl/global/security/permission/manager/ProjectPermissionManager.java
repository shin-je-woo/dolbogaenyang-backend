package com.whatpl.global.security.permission.manager;

import com.whatpl.global.exception.BizException;
import com.whatpl.global.exception.ErrorCode;
import com.whatpl.global.security.domain.MemberPrincipal;
import com.whatpl.domain.project.domain.Project;
import com.whatpl.domain.project.repository.project.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class ProjectPermissionManager implements WhatplPermissionManager {

    private final ProjectRepository projectRepository;

    @Override
    @Transactional(readOnly = true)
    public boolean hasPrivilege(MemberPrincipal memberPrincipal, Long targetId, String permission) {
        return switch (permission) {
            case "UPDATE" -> hasUpdatePrivilege(memberPrincipal, targetId);
            case "DELETE" -> hasDeletePrivilege(memberPrincipal, targetId);
            default -> false;
        };
    }

    /**
     * 프로젝트 수정 권한
     * 프로젝트 모집자(등록자)
     */
    private boolean hasUpdatePrivilege(MemberPrincipal memberPrincipal, Long projectId) {
        Project project = projectRepository.findWithWriterById(projectId)
                .orElseThrow(() -> new BizException(ErrorCode.NOT_FOUND_PROJECT));
        return Objects.equals(project.getWriter().getId(), memberPrincipal.getId());
    }

    /**
     * 프로젝트 삭제 권한
     * 프로젝트 모집자(등록자)
     */
    private boolean hasDeletePrivilege(MemberPrincipal memberPrincipal, Long projectId) {
        Project project = projectRepository.findWithWriterById(projectId)
                .orElseThrow(() -> new BizException(ErrorCode.NOT_FOUND_PROJECT));
        return Objects.equals(project.getWriter().getId(), memberPrincipal.getId());
    }
}
