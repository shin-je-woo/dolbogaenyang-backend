package com.whatpl.domain.project.service;

import com.whatpl.domain.member.domain.Member;
import com.whatpl.domain.member.model.MemberFixture;
import com.whatpl.domain.member.repository.MemberRepository;
import com.whatpl.domain.project.domain.Apply;
import com.whatpl.domain.project.domain.Project;
import com.whatpl.domain.project.domain.ProjectParticipant;
import com.whatpl.domain.project.domain.RecruitJob;
import com.whatpl.domain.project.model.ProjectStatus;
import com.whatpl.domain.project.dto.ProjectApplyRequest;
import com.whatpl.domain.project.model.*;
import com.whatpl.domain.project.repository.ApplyRepository;
import com.whatpl.domain.project.repository.ProjectRepository;
import com.whatpl.global.common.model.ApplyStatus;
import com.whatpl.global.common.model.Job;
import com.whatpl.global.exception.BizException;
import com.whatpl.global.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
        ProjectApplyRequest request = ProjectApplyRequestFixture.apply(Job.BACKEND_DEVELOPER);
        when(projectRepository.findWithRecruitJobsById(anyLong()))
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
        ProjectApplyRequest request = ProjectApplyRequestFixture.apply(Job.BACKEND_DEVELOPER);
        when(projectRepository.findWithRecruitJobsById(anyLong()))
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
        // given
        Project project = ProjectFixture.create();
        Member writer = MemberFixture.onlyRequired();
        project.addRepresentImageAndWriter(null, writer);
        project.setStatus(ProjectStatus.RECRUITING);
        ProjectApplyRequest request = ProjectApplyRequestFixture.apply(Job.BACKEND_DEVELOPER);
        when(projectRepository.findWithRecruitJobsById(anyLong()))
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
        // given
        Project project = ProjectFixture.create();
        Member writer = MemberFixture.onlyRequired();
        project.addRepresentImageAndWriter(null, writer);
        project.setStatus(ProjectStatus.RECRUITING);
        project.addRecruitJob(new RecruitJob(Job.DESIGNER, 5));

        ProjectApplyRequest request = ProjectApplyRequestFixture.apply(Job.BACKEND_DEVELOPER);
        when(projectRepository.findWithRecruitJobsById(anyLong()))
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
        // given
        int recruitAmount = 1;
        Project project = ProjectFixture.create();
        Member writer = MemberFixture.onlyRequired();
        Member applicant = MemberFixture.withAll();
        ProjectParticipant participant = ProjectParticipantFixture.create(Job.BACKEND_DEVELOPER, applicant);
        project.addProjectParticipant(participant);
        project.addRepresentImageAndWriter(null, writer);
        project.setStatus(ProjectStatus.RECRUITING);
        project.addRecruitJob(new RecruitJob(Job.BACKEND_DEVELOPER, recruitAmount));

        ProjectApplyRequest request = ProjectApplyRequestFixture.apply(Job.BACKEND_DEVELOPER);
        when(projectRepository.findWithRecruitJobsById(anyLong()))
                .thenReturn(Optional.of(project));
        when(memberRepository.findById(anyLong()))
                .thenReturn(Optional.of(applicant));
//        when(projectParticipantRepository.countByProjectIdAndJob(any(), any()))
//                .thenReturn(5);

        // when & then
        BizException bizException = assertThrows(BizException.class, () ->
                projectApplyService.apply(request, 0L, 0L));
        assertEquals(ErrorCode.RECRUIT_COMPLETED_APPLY_JOB, bizException.getErrorCode());
    }

    @Test
    @DisplayName("이미 지원한 프로젝트에 지원하면 실패")
    void apply_duplicated_apply_project() {
        // given
        Project project = ProjectFixture.create();
        Member writer = MemberFixture.onlyRequired();
        Member applicant = MemberFixture.withAll();
        Apply apply = ApplyFixture.waiting(Job.BACKEND_DEVELOPER, applicant, project); // 현재상태 WAITING
        project.addApply(apply);
        project.addRepresentImageAndWriter(null, writer);
        project.setStatus(ProjectStatus.RECRUITING);
        project.addRecruitJob(new RecruitJob(Job.BACKEND_DEVELOPER, 5));

        ProjectApplyRequest request = ProjectApplyRequestFixture.apply(Job.BACKEND_DEVELOPER);
        when(projectRepository.findWithRecruitJobsById(anyLong()))
                .thenReturn(Optional.of(project));
        when(memberRepository.findById(anyLong()))
                .thenReturn(Optional.of(applicant));

        // when & then
        BizException bizException = assertThrows(BizException.class, () ->
                projectApplyService.apply(request, 0L, 0L));
        assertEquals(ErrorCode.DUPLICATED_APPLY, bizException.getErrorCode());
    }

    @Test
    @DisplayName("프로젝트 지원서 승인할 경우 승인 완료상태로 변경되고, project_participant 데이터가 추가된다.")
    void status_accept() {
        // given
        Member applicant = MemberFixture.onlyRequired();
        RecruitJob recruitJob = RecruitJobFixture.create(Job.BACKEND_DEVELOPER);
        Project project = Mockito.spy(ProjectFixture.withRecruitJobs(recruitJob));
        Apply apply = Mockito.spy(ApplyFixture.waiting(Job.BACKEND_DEVELOPER, applicant, project)); // 현재상태 WAITING
        when(project.getId()).thenReturn(1L);
        when(apply.getId()).thenReturn(1L);
        when(projectRepository.findWithRecruitJobsById(anyLong()))
                .thenReturn(Optional.of(project));
        when(applyRepository.findWithProjectAndApplicantById(anyLong()))
                .thenReturn(Optional.of(apply));

        // when
        projectApplyService.status(project.getId(), apply.getId(), ApplyStatus.ACCEPTED);

        // then
        assertEquals(ApplyStatus.ACCEPTED, apply.getStatus());
        assertEquals(project.getProjectParticipants().size(), 1);
    }

    @Test
    @DisplayName("프로젝트 지원서 승인 시 모집인원을 초과할 경우 승인 실패")
    void status_accept_full() {
        // given
        Member applicant = MemberFixture.onlyRequired();
        RecruitJob recruitJob = RecruitJobFixture.withRecruitAmount(Job.BACKEND_DEVELOPER, 1);
        Project project = Mockito.spy(ProjectFixture.withRecruitJobs(recruitJob));
        Apply apply = Mockito.spy(ApplyFixture.waiting(Job.BACKEND_DEVELOPER, applicant, project)); // 현재상태 WAITING
        ProjectParticipant participant = ProjectParticipantFixture.create(Job.BACKEND_DEVELOPER, MemberFixture.withAll());
        project.addProjectParticipant(participant);
        when(project.getId()).thenReturn(1L);
        when(apply.getId()).thenReturn(1L);
        when(projectRepository.findWithRecruitJobsById(anyLong()))
                .thenReturn(Optional.of(project));
        when(applyRepository.findWithProjectAndApplicantById(anyLong()))
                .thenReturn(Optional.of(apply));

        // when & then
        BizException bizException = assertThrows(BizException.class, () ->
                projectApplyService.status(project.getId(), apply.getId(), ApplyStatus.ACCEPTED));
        assertEquals(bizException.getErrorCode(), ErrorCode.RECRUIT_COMPLETED_APPLY_JOB);
    }

    @Test
    @DisplayName("프로젝트 지원서 거절할 경우 승인 거절상태로 변경된다.")
    void status_reject() {
        // given
        Member applicant = MemberFixture.onlyRequired();
        RecruitJob recruitJob = RecruitJobFixture.create(Job.BACKEND_DEVELOPER);
        Project project = Mockito.spy(ProjectFixture.withRecruitJobs(recruitJob));
        when(project.getId()).thenReturn(1L);
        Apply apply = Mockito.spy(ApplyFixture.waiting(Job.BACKEND_DEVELOPER, applicant, project));
        when(apply.getId()).thenReturn(1L);
        when(projectRepository.findWithRecruitJobsById(anyLong()))
                .thenReturn(Optional.of(project));
        when(applyRepository.findWithProjectAndApplicantById(anyLong()))
                .thenReturn(Optional.of(apply));

        // when
        projectApplyService.status(project.getId(), apply.getId(), ApplyStatus.REJECTED);

        // then
        assertEquals(ApplyStatus.REJECTED, apply.getStatus());
    }

    @Test
    @DisplayName("이미 처리된 프로젝트 지원서는 승인/거절 불가")
    void status_already_processed() {
        // given
        Member applicant = MemberFixture.onlyRequired();
        RecruitJob recruitJob = RecruitJobFixture.create(Job.BACKEND_DEVELOPER);
        Project project = Mockito.spy(ProjectFixture.withRecruitJobs(recruitJob));
        when(project.getId()).thenReturn(1L);
        Apply apply = Mockito.spy(ApplyFixture.accepted(Job.BACKEND_DEVELOPER, applicant, project));
        when(apply.getId()).thenReturn(1L);
        when(projectRepository.findWithRecruitJobsById(anyLong()))
                .thenReturn(Optional.of(project));
        when(applyRepository.findWithProjectAndApplicantById(anyLong()))
                .thenReturn(Optional.of(apply));

        // when & then
        BizException bizException = assertThrows(BizException.class, () ->
                projectApplyService.status(project.getId(), apply.getId(), ApplyStatus.REJECTED));
        assertEquals(ErrorCode.ALREADY_PROCESSED_APPLY, bizException.getErrorCode());
    }
}