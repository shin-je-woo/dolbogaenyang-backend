package com.whatpl.domain.project.service;

import com.whatpl.global.aop.annotation.DistributedLock;
import com.whatpl.global.common.domain.enums.ApplyStatus;
import com.whatpl.global.exception.BizException;
import com.whatpl.global.exception.ErrorCode;
import com.whatpl.domain.project.domain.Project;
import com.whatpl.domain.project.domain.ProjectParticipant;
import com.whatpl.domain.project.repository.ApplyRepository;
import com.whatpl.domain.project.repository.ProjectParticipantRepository;
import com.whatpl.domain.project.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProjectParticipantService {

    private final ProjectRepository projectRepository;
    private final ProjectParticipantRepository projectParticipantRepository;
    private final ApplyRepository applyRepository;

    @Transactional
    @DistributedLock(name = "'project:'.concat(#projectId)")
    @CacheEvict(value = "projectList", allEntries = true)
    public void deleteParticipant(final long projectId, final long participantId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new BizException(ErrorCode.NOT_FOUND_PROJECT));
        ProjectParticipant projectParticipant = projectParticipantRepository.findWithAllById(participantId)
                .orElseThrow(() -> new BizException(ErrorCode.NOT_FOUND_PROJECT_PARTICIPANT));
        if (!project.getId().equals(projectParticipant.getProject().getId())) {
            throw new BizException(ErrorCode.NOT_MATCH_PROJECT_PARTICIPANT);
        }
        // 지원서의 상태를 승인 -> 제외로 변경
        applyRepository.findByApplicantAndStatus(projectParticipant.getParticipant(), ApplyStatus.ACCEPTED)
                .ifPresent(apply -> apply.changeStatus(ApplyStatus.EXCLUDED));

        projectParticipantRepository.delete(projectParticipant);
    }
}
