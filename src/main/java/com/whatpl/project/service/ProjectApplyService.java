package com.whatpl.project.service;

import com.whatpl.global.common.domain.enums.Job;
import com.whatpl.global.exception.BizException;
import com.whatpl.global.exception.ErrorCode;
import com.whatpl.global.security.domain.MemberPrincipal;
import com.whatpl.member.domain.Member;
import com.whatpl.member.repository.MemberRepository;
import com.whatpl.project.converter.ApplyModelConverter;
import com.whatpl.project.domain.Apply;
import com.whatpl.project.domain.Project;
import com.whatpl.project.domain.RecruitJob;
import com.whatpl.project.domain.enums.ApplyStatus;
import com.whatpl.project.domain.enums.ProjectStatus;
import com.whatpl.project.dto.ProjectApplyReadResponse;
import com.whatpl.project.dto.ProjectApplyRequest;
import com.whatpl.project.repository.ApplyRepository;
import com.whatpl.project.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ProjectApplyService {

    private final MemberRepository memberRepository;
    private final ProjectRepository projectRepository;
    private final ApplyRepository applyRepository;

    @Transactional
    public Long apply(final ProjectApplyRequest request, final long projectId, final long applicantId) {
        Project project = projectRepository.findWithRecruitJobsById(projectId)
                .orElseThrow(() -> new BizException(ErrorCode.NOT_FOUND_PROJECT));
        Member applicant = memberRepository.findById(applicantId)
                .orElseThrow(() -> new BizException(ErrorCode.NOT_FOUND_MEMBER));

        // 삭제된 프로젝트는 지원 불가
        validateDeletedProject(project);
        // 모집완료된 프로젝트는 지원 불가
        validateCompletedRecruitment(project);
        // 프로젝트 등록자는 본인이 등록한 프로젝트에 지원 불가
        validateWriter(project, applicant);
        // 모집직군에 지원하는 직무가 없을 경우, 모집직군에 지원하는 직무가 마감된 경우 지원 불가
        validateFullJob(project, request.getApplyJob());
        // 이미 지원한 프로젝트는 지원 불가
        validateDuplicatedApply(project, applicant);

        Apply apply = Apply.of(request.getApplyJob(), request.getContent(), applicant, project);
        Apply savedApply = applyRepository.save(apply);
        return savedApply.getId();
    }

    @Transactional
    public ProjectApplyReadResponse read(final long projectId, final long applyId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new BizException(ErrorCode.NOT_FOUND_PROJECT));
        Apply apply = applyRepository.findWithProjectAndApplicantById(applyId)
                .orElseThrow(() -> new BizException(ErrorCode.NOT_FOUND_APPLY));

        if (!project.getId().equals(apply.getProject().getId())) {
            throw new BizException(ErrorCode.NOT_MATCH_PROJECT_APPLY);
        }

        // 모집자가 지원서를 읽을 경우 조회한 시간 update
        MemberPrincipal principal = (MemberPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal.getId() == project.getWriter().getId() && apply.getRecruiterReadAt() == null) {
            apply.setRecruiterReadAt(LocalDateTime.now());
        }

        return ApplyModelConverter.convert(apply);
    }

    @Transactional
    public void status(final long projectId, final long applyId, final ApplyStatus applyStatus) {
        Project project = projectRepository.findWithRecruitJobsById(projectId)
                .orElseThrow(() -> new BizException(ErrorCode.NOT_FOUND_PROJECT));
        Apply apply = applyRepository.findWithProjectAndApplicantById(applyId)
                .orElseThrow(() -> new BizException(ErrorCode.NOT_FOUND_APPLY));

        if (!project.getId().equals(apply.getProject().getId())) {
            throw new BizException(ErrorCode.NOT_MATCH_PROJECT_APPLY);
        }

        RecruitJob recruitJob = project.getRecruitJobs().stream()
                .filter(rj -> rj.getJob().equals(apply.getJob()))
                .findFirst()
                .orElseThrow(() -> new BizException(ErrorCode.NOT_MATCH_APPLY_JOB_WITH_PROJECT));

        // 지원서의 상태를 승인/거절 상태로 변경한다. 만약, 이미 처리된 지원서라면 BizException 발생
        apply.changeStatus(applyStatus);
        if (applyStatus.equals(ApplyStatus.ACCEPTED)) {
            // 지원서 승인일 경우 프로젝트 모집직군의 현재 인원을 증가한다. 만약, 모집인원이 초과될 경우 BizException 발생
            recruitJob.increaseCurrentAmount();
        }
    }

    private void validateDeletedProject(Project project) {
        if (project.getStatus().equals(ProjectStatus.DELETED)) {
            throw new BizException(ErrorCode.DELETED_PROJECT);
        }
    }

    private void validateCompletedRecruitment(Project project) {
        if (project.getStatus().equals(ProjectStatus.COMPLETED)) {
            throw new BizException(ErrorCode.COMPLETED_RECRUITMENT);
        }
    }

    private void validateWriter(Project project, Member applicant) {
        if (project.getWriter().equals(applicant)) {
            throw new BizException(ErrorCode.WRITER_NOT_APPLY);
        }
    }

    private void validateFullJob(Project project, Job applyJob) {
        RecruitJob matchedRecruitJob = project.getRecruitJobs().stream()
                .filter(recruitJob -> recruitJob.getJob().equals(applyJob))
                .findFirst()
                .orElseThrow(() -> new BizException(ErrorCode.NOT_MATCH_APPLY_JOB_WITH_PROJECT));
        if (matchedRecruitJob.isFullJob()) {
            throw new BizException(ErrorCode.RECRUIT_COMPLETED_APPLY_JOB);
        }
    }

    private void validateDuplicatedApply(Project project, Member applicant) {
        boolean isDuplicated = applyRepository.findByProjectAndApplicant(project, applicant)
                .isPresent();

        if (isDuplicated) {
            throw new BizException(ErrorCode.DUPLICATED_APPLY);
        }
    }
}
