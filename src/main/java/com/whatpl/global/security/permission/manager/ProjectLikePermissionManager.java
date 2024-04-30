package com.whatpl.global.security.permission.manager;

import com.whatpl.global.exception.BizException;
import com.whatpl.global.exception.ErrorCode;
import com.whatpl.global.security.domain.MemberPrincipal;
import com.whatpl.project.domain.ProjectLike;
import com.whatpl.project.repository.ProjectLikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

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
    private boolean hasDeletePrivilege(MemberPrincipal memberPrincipal, Long likeId) {
        ProjectLike projectLike = projectLikeRepository.findWithMemberById(likeId)
                .orElseThrow(() -> new BizException(ErrorCode.NOT_FOUND_DATA));

        return projectLike.getMember().getId().equals(memberPrincipal.getId());
    }
}
