package com.whatpl.project.service;

import com.whatpl.global.common.domain.enums.Job;
import com.whatpl.global.exception.BizException;
import com.whatpl.global.exception.ErrorCode;
import com.whatpl.member.domain.Member;
import com.whatpl.member.model.MemberFixture;
import com.whatpl.member.repository.MemberRepository;
import com.whatpl.project.domain.Apply;
import com.whatpl.project.domain.Project;
import com.whatpl.project.domain.RecruitJob;
import com.whatpl.project.domain.enums.ApplyStatus;
import com.whatpl.project.domain.enums.ProjectStatus;
import com.whatpl.project.dto.ProjectApplyRequest;
import com.whatpl.project.model.ProjectFixture;
import com.whatpl.project.repository.ApplyRepository;
import com.whatpl.project.repository.ProjectRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectApplyServiceTest {

    @InjectMocks
    private ProjectApplyService projectApplyService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ApplyRepository applyRepository;

    @Test
    @DisplayName("삭제된 프로젝트에 지원하면 실패")
    void apply_deleted_project() {
        // given
        Project project = ProjectFixture.create();
        Member writer = MemberFixture.onlyRequired();
        project.setStatus(ProjectStatus.DELETED);
        ProjectApplyRequest request = new ProjectApplyRequest(Job.BACKEND_DEVELOPER, "test content");
        when(projectRepository.findByIdWithRecruitJobs(anyLong()))
                .thenReturn(Optional.of(project));
        when(memberRepository.findById(anyLong()))
                .thenReturn(Optional.of(writer));

        // when & then
        BizException bizException = assertThrows(BizException.class, () ->
                projectApplyService.apply(request, 0L, 0L));
        assertEquals(ErrorCode.DELETED_PROJECT, bizException.getErrorCode());
    }

    @Test
    @DisplayName("모집완료된 프로젝트에 지원하면 실패")
    void apply_completed_project() {
        // given
        Project project = ProjectFixture.create();
        Member writer = MemberFixture.onlyRequired();
        project.setStatus(ProjectStatus.COMPLETED);
        ProjectApplyRequest request = new ProjectApplyRequest(Job.BACKEND_DEVELOPER, "test content");
        when(projectRepository.findByIdWithRecruitJobs(anyLong()))
                .thenReturn(Optional.of(project));
        when(memberRepository.findById(anyLong()))
                .thenReturn(Optional.of(writer));

        // when & then
        BizException bizException = assertThrows(BizException.class, () ->
                projectApplyService.apply(request, 0L, 0L));
        assertEquals(ErrorCode.COMPLETED_RECRUITMENT, bizException.getErrorCode());
    }

    @Test
    @DisplayName("본인이 등록한 프로젝트에 지원하면 실패")
    void apply_writer_equals_applicant() {
        Project project = ProjectFixture.create();
        Member writer = MemberFixture.onlyRequired();
        project.addRepresentImageAndWriter(null, writer);
        project.setStatus(ProjectStatus.RECRUITING);
        ProjectApplyRequest request = new ProjectApplyRequest(Job.BACKEND_DEVELOPER, "test content");
        when(projectRepository.findByIdWithRecruitJobs(anyLong()))
                .thenReturn(Optional.of(project));
        when(memberRepository.findById(anyLong()))
                .thenReturn(Optional.of(writer));

        // when & then
        BizException bizException = assertThrows(BizException.class, () ->
                projectApplyService.apply(request, 0L, 0L));
        assertEquals(ErrorCode.WRITER_NOT_APPLY, bizException.getErrorCode());
    }

    @Test
    @DisplayName("모집직군에 지원하는 직무가 없으면 실패")
    void apply_has_not_match_job_project() {
        Project project = ProjectFixture.create();
        Member writer = MemberFixture.onlyRequired();
        project.addRepresentImageAndWriter(null, writer);
        project.setStatus(ProjectStatus.RECRUITING);
        project.addRecruitJob(new RecruitJob(Job.DESIGNER, 5, 0));

        ProjectApplyRequest request = new ProjectApplyRequest(Job.BACKEND_DEVELOPER, "test content");
        when(projectRepository.findByIdWithRecruitJobs(anyLong()))
                .thenReturn(Optional.of(project));
        when(memberRepository.findById(anyLong()))
                .thenReturn(Optional.of(MemberFixture.withAll()));

        // when & then
        BizException bizException = assertThrows(BizException.class, () ->
                projectApplyService.apply(request, 0L, 0L));
        assertEquals(ErrorCode.NOT_MATCH_APPLY_JOB_WITH_PROJECT, bizException.getErrorCode());
    }

    @Test
    @DisplayName("모집직군에 지원하는 직무가 마감된 경우 실패")
    void apply_full_job_project() {
        Project project = ProjectFixture.create();
        Member writer = MemberFixture.onlyRequired();
        project.addRepresentImageAndWriter(null, writer);
        project.setStatus(ProjectStatus.RECRUITING);
        project.addRecruitJob(new RecruitJob(Job.BACKEND_DEVELOPER, 5, 5));

        ProjectApplyRequest request = new ProjectApplyRequest(Job.BACKEND_DEVELOPER, "test content");
        when(projectRepository.findByIdWithRecruitJobs(anyLong()))
                .thenReturn(Optional.of(project));
        when(memberRepository.findById(anyLong()))
                .thenReturn(Optional.of(MemberFixture.withAll()));

        // when & then
        BizException bizException = assertThrows(BizException.class, () ->
                projectApplyService.apply(request, 0L, 0L));
        assertEquals(ErrorCode.RECRUIT_COMPLETED_APPLY_JOB, bizException.getErrorCode());
    }

    @Test
    @DisplayName("이미 지원한 프로젝트에 지원하면 실패")
    void apply_duplicated_apply_project() {
        Project project = ProjectFixture.create();
        Member writer = MemberFixture.onlyRequired();
        project.addRepresentImageAndWriter(null, writer);
        project.setStatus(ProjectStatus.RECRUITING);
        project.addRecruitJob(new RecruitJob(Job.BACKEND_DEVELOPER, 5, 0));

        ProjectApplyRequest request = new ProjectApplyRequest(Job.BACKEND_DEVELOPER, "test content");
        when(projectRepository.findByIdWithRecruitJobs(anyLong()))
                .thenReturn(Optional.of(project));
        when(memberRepository.findById(anyLong()))
                .thenReturn(Optional.of(MemberFixture.withAll()));
        when(applyRepository.findByProjectAndApplicant(any(Project.class), any(Member.class)))
                .thenReturn(Optional.of(new Apply(Job.BACKEND_DEVELOPER, "test content",
                        ApplyStatus.WAITING, MemberFixture.withAll(), project)));

        // when & then
        BizException bizException = assertThrows(BizException.class, () ->
                projectApplyService.apply(request, 0L, 0L));
        assertEquals(ErrorCode.DUPLICATED_APPLY, bizException.getErrorCode());
    }
}