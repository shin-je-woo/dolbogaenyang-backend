package com.whatpl.domain.project.service;

import com.whatpl.domain.member.domain.Member;
import com.whatpl.domain.member.repository.MemberRepository;
import com.whatpl.domain.project.domain.Apply;
import com.whatpl.domain.project.domain.Project;
import com.whatpl.domain.project.dto.ApplyResponse;
import com.whatpl.domain.project.dto.ProjectApplyRequest;
import com.whatpl.domain.project.event.ProjectApplyEvent;
import com.whatpl.domain.project.repository.apply.ApplyRepository;
import com.whatpl.domain.project.repository.project.ProjectRepository;
import com.whatpl.global.aop.annotation.DistributedLock;
import com.whatpl.global.common.model.ApplyStatus;
import com.whatpl.global.exception.BizException;
import com.whatpl.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProjectApplyService {

    private final MemberRepository memberRepository;
    private final ProjectRepository projectRepository;
    private final ApplyRepository applyRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    @DistributedLock(name = "'project:'.concat(#projectId)")
    public ApplyResponse apply(final ProjectApplyRequest request, final long projectId, final long applicantId) {
        Project project = projectRepository.findWithRecruitJobsById(projectId)
                .orElseThrow(() -> new BizException(ErrorCode.NOT_FOUND_PROJECT));
        Member applicant = memberRepository.findById(applicantId)
                .orElseThrow(() -> new BizException(ErrorCode.NOT_FOUND_MEMBER));
        Apply apply = project.applyOrOffer(request.getApplyType(), request.getApplyJob(), applicant);
        Apply savedApply = applyRepository.save(apply);
        eventPublisher.publishEvent(ProjectApplyEvent.of(apply, request.getContent()));

        return ApplyResponse.builder()
                .applyId(savedApply.getId())
                .projectId(savedApply.getProject().getId())
                .chatRoomId(savedApply.getChatRoom().getId())
                .build();
    }

    @Transactional
    @DistributedLock(name = "'project:'.concat(#projectId)")
    @CacheEvict(value = "projectList", allEntries = true,
            condition = "T(com.whatpl.global.common.model.ApplyStatus).ACCEPTED.equals(#applyStatus)")
    public void status(final long projectId, final long applyId, final ApplyStatus applyStatus) {
        Project project = projectRepository.findWithRecruitJobsById(projectId)
                .orElseThrow(() -> new BizException(ErrorCode.NOT_FOUND_PROJECT));
        Apply apply = applyRepository.findWithProjectAndApplicantById(applyId)
                .orElseThrow(() -> new BizException(ErrorCode.NOT_FOUND_APPLY));
        if (!project.getId().equals(apply.getProject().getId())) {
            throw new BizException(ErrorCode.NOT_MATCH_PROJECT_APPLY);
        }
        apply.processApply(project, applyStatus);
    }
}
