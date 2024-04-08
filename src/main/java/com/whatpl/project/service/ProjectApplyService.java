package com.whatpl.project.service;

import com.whatpl.global.common.domain.enums.Job;
import com.whatpl.global.exception.BizException;
import com.whatpl.global.exception.ErrorCode;
import com.whatpl.member.domain.Member;
import com.whatpl.member.repository.MemberRepository;
import com.whatpl.project.domain.Apply;
import com.whatpl.project.domain.Project;
import com.whatpl.project.domain.RecruitJob;
import com.whatpl.project.domain.enums.ProjectStatus;
import com.whatpl.project.dto.ProjectApplyRequest;
import com.whatpl.project.repository.ApplyRepository;
import com.whatpl.project.repository.ProjectRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProjectApplyService {

    private final MemberRepository memberRepository;
    private final ProjectRepository projectRepository;
    private final ApplyRepository applyRepository;

    @Transactional
    public Long apply(final ProjectApplyRequest request, final long projectId, final long applicantId) {
        Project project = projectRepository.findByIdWithRecruitJobs(projectId)
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
