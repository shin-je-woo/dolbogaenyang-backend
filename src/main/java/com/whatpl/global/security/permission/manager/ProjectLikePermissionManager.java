package com.whatpl.global.security.permission.manager;

import com.whatpl.global.exception.BizException;
import com.whatpl.global.exception.ErrorCode;
import com.whatpl.global.security.domain.MemberPrincipal;
import com.whatpl.domain.project.domain.ProjectLike;
import com.whatpl.domain.project.repository.ProjectLikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class ProjectLikePermissionManager implements WhatplPermissionManager{

    private final ProjectLikeRepository projectLikeRepository;

    @Override
    public boolean hasPrivilege(MemberPrincipal memberPrincipal, Long targetId, String permission) {
        if (permission.equals("DELETE")) {
            return hasDeletePrivilege(memberPrincipal, targetId);
        }
        return false;
    }

    /**
     * 프로젝트 좋아요 삭제 권한
     * 좋아요 등록한 사용자
     */
    private boolean hasDeletePrivilege(MemberPrincipal memberPrincipal, Long projectId) {
        ProjectLike projectLike = projectLikeRepository.findByProjectIdAndMemberId(projectId, memberPrincipal.getId())
                .orElseThrow(() -> new BizException(ErrorCode.NOT_FOUND_DATA));

        return Objects.equals(memberPrincipal.getId(), projectLike.getMember().getId());
    }
}
