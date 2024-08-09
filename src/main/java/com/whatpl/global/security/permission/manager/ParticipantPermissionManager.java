package com.whatpl.global.security.permission.manager;

import com.whatpl.global.exception.BizException;
import com.whatpl.global.exception.ErrorCode;
import com.whatpl.global.security.domain.MemberPrincipal;
import com.whatpl.domain.project.domain.ProjectParticipant;
import com.whatpl.domain.project.repository.ProjectParticipantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ParticipantPermissionManager implements WhatplPermissionManager {

    private final ProjectParticipantRepository projectParticipantRepository;

    @Override
    @Transactional(readOnly = true)
    public boolean hasPrivilege(MemberPrincipal memberPrincipal, Long targetId, String permission) {
        return switch (permission) {
            case "DELETE" -> hasDeletePrivilege(memberPrincipal, targetId);
            default -> false;
        };
    }

    /**
     * 프로젝트 참여자 삭제 권한
     * 프로젝트 등록자
     */
    private boolean hasDeletePrivilege(MemberPrincipal memberPrincipal, Long participantId) {
        ProjectParticipant projectParticipant = projectParticipantRepository.findWithAllById(participantId)
                .orElseThrow(() -> new BizException(ErrorCode.NOT_FOUND_PROJECT_PARTICIPANT));
        Long recruiterId = projectParticipant.getProject().getWriter().getId();
        return recruiterId.equals(memberPrincipal.getId());
    }
}
