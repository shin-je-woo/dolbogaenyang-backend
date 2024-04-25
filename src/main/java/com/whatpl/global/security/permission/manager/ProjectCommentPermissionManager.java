package com.whatpl.global.security.permission.manager;

import com.whatpl.global.exception.BizException;
import com.whatpl.global.exception.ErrorCode;
import com.whatpl.global.security.domain.MemberPrincipal;
import com.whatpl.project.domain.ProjectComment;
import com.whatpl.project.repository.ProjectCommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ProjectCommentPermissionManager implements WhatplPermissionManager {

    private final ProjectCommentRepository projectCommentRepository;

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
     * 프로젝트 댓글 수정 권한
     * 댓글 등록자
     */
    private boolean hasUpdatePrivilege(MemberPrincipal memberPrincipal, Long commentId) {
        ProjectComment projectComment = projectCommentRepository.findById(commentId)
                .orElseThrow(() -> new BizException(ErrorCode.NOT_FOUND_PROJECT_COMMENT));

        return projectComment.getWriter().getId().equals(memberPrincipal.getId());
    }

    /**
     * 프로젝트 댓글 삭제 권한
     * 댓글 등록자
     */
    private boolean hasDeletePrivilege(MemberPrincipal memberPrincipal, Long commentId) {
        ProjectComment projectComment = projectCommentRepository.findById(commentId)
                .orElseThrow(() -> new BizException(ErrorCode.NOT_FOUND_PROJECT_COMMENT));

        return projectComment.getWriter().getId().equals(memberPrincipal.getId());
    }
}
