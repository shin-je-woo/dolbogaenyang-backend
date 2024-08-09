package com.whatpl.domain.project.service;

import com.whatpl.domain.attachment.domain.Attachment;
import com.whatpl.domain.attachment.repository.AttachmentRepository;
import com.whatpl.global.common.model.Job;
import com.whatpl.global.exception.BizException;
import com.whatpl.global.exception.ErrorCode;
import com.whatpl.domain.member.model.MemberFixture;
import com.whatpl.domain.member.repository.MemberRepository;
import com.whatpl.domain.project.domain.Project;
import com.whatpl.domain.project.domain.ProjectParticipant;
import com.whatpl.domain.project.domain.RecruitJob;
import com.whatpl.domain.project.dto.ProjectCreateRequest;
import com.whatpl.domain.project.dto.ProjectUpdateRequest;
import com.whatpl.domain.project.dto.RecruitJobField;
import com.whatpl.domain.project.model.ProjectCreateRequestFixture;
import com.whatpl.domain.project.model.ProjectFixture;
import com.whatpl.domain.project.model.ProjectParticipantFixture;
import com.whatpl.domain.project.model.RecruitJobFixture;
import com.whatpl.domain.project.repository.ProjectRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectWriteServiceTest {

    @InjectMocks
    ProjectWriteService projectWriteService;

    @Mock
    MemberRepository memberRepository;

    @Mock
    AttachmentRepository attachmentRepository;

    @Mock
    ProjectRepository projectRepository;

    @Test
    @DisplayName("프로젝트 등록 시 대표 이미지가 이미지 타입이 아니면 실패")
    void createProject_not_image() {
        // given
        ProjectCreateRequest projectCreateRequest = ProjectCreateRequestFixture.create();
        Attachment pdfFile = Attachment.builder().mimeType(MediaType.APPLICATION_PDF_VALUE).build();
        when(attachmentRepository.findById(anyLong()))
                .thenReturn(Optional.of(pdfFile));
        when(memberRepository.findById(anyLong()))
                .thenReturn(Optional.of(MemberFixture.withAll()));

        // when & then
        BizException bizException = assertThrows(BizException.class, () ->
                projectWriteService.createProject(projectCreateRequest, anyLong()));
        assertEquals(ErrorCode.NOT_IMAGE_FILE, bizException.getErrorCode());
    }

    @Test
    @DisplayName("프로젝트 참여자가 존재하는 모집직군은 삭제 불가능")
    void cant_delete_recruit_job_exists_participant() {
        // given
        List<RecruitJob> recruitJobs = List.of(RecruitJobFixture.create(Job.BACKEND_DEVELOPER));
        List<ProjectParticipant> participants = List.of(ProjectParticipantFixture.create(Job.BACKEND_DEVELOPER, MemberFixture.onlyRequired()));
        Project project = ProjectFixture.withRecruitJobAndParticipant(recruitJobs, participants);
        when(projectRepository.findWithRecruitJobsById(anyLong()))
                .thenReturn(Optional.of(project));
        ProjectUpdateRequest request = ProjectUpdateRequest.builder().build(); // 모집직군 없는 요청

        // when & then
        BizException bizException = assertThrows(BizException.class, () ->
                projectWriteService.modifyProject(anyLong(), request));
        assertEquals(ErrorCode.CANT_DELETE_RECRUIT_JOB_EXISTS_PARTICIPANT, bizException.getErrorCode());
    }

    @Test
    @DisplayName("모집인원이 참여자 수보다 적을 경우 에러")
    void recruit_amount_cant_less_then_participant_amount() {
        // given
        List<RecruitJob> recruitJobs = List.of(RecruitJobFixture.create(Job.BACKEND_DEVELOPER));
        List<ProjectParticipant> participants = List.of(
                ProjectParticipantFixture.create(Job.BACKEND_DEVELOPER, MemberFixture.onlyRequired()),
                ProjectParticipantFixture.create(Job.BACKEND_DEVELOPER, MemberFixture.onlyRequired())
        );
        Project project = ProjectFixture.withRecruitJobAndParticipant(recruitJobs, participants);
        when(projectRepository.findWithRecruitJobsById(anyLong()))
                .thenReturn(Optional.of(project));
        ProjectUpdateRequest request = ProjectUpdateRequest.builder()
                .recruitJobs(Set.of(new RecruitJobField(Job.BACKEND_DEVELOPER, 1))) // 참여자 수보다 적은 모집인원 요청
                .build();

        // when & then
        BizException bizException = assertThrows(BizException.class, () ->
                projectWriteService.modifyProject(anyLong(), request));
        assertEquals(ErrorCode.RECRUIT_AMOUNT_CANT_LESS_THEN_PARTICIPANT_AMOUNT, bizException.getErrorCode());
    }

    @Test
    @DisplayName("기존에 존재하지 않던 모집직군 추가")
    void merge_recruit_jobs() {
        // given
        Project project = ProjectFixture.withRecruitJobs(RecruitJobFixture.create(Job.BACKEND_DEVELOPER));
        when(projectRepository.findWithRecruitJobsById(anyLong()))
                .thenReturn(Optional.of(project));
        ProjectUpdateRequest request = ProjectUpdateRequest.builder()
                .recruitJobs(Set.of(
                        new RecruitJobField(Job.BACKEND_DEVELOPER, 1),
                        new RecruitJobField(Job.FRONTEND_DEVELOPER, 1))) // 기존에 존재하지 않던 모집직군 추가 요청
                .build();

        // when
        projectWriteService.modifyProject(anyLong(), request);

        // then
        assertEquals(project.getRecruitJobs().size(), 2);
    }

    @Test
    @DisplayName("기존에 존재하던 모집직군 삭제")
    void delete_recruit_jobs() {
        // given
        Project project = ProjectFixture.withRecruitJobs(
                RecruitJobFixture.create(Job.BACKEND_DEVELOPER),
                RecruitJobFixture.create(Job.FRONTEND_DEVELOPER));
        when(projectRepository.findWithRecruitJobsById(anyLong()))
                .thenReturn(Optional.of(project));
        ProjectUpdateRequest request = ProjectUpdateRequest.builder()
                .recruitJobs(Set.of(new RecruitJobField(Job.BACKEND_DEVELOPER, 1))) // 기존에 존재하던 모집직군 삭제 요청
                .build();

        // when
        projectWriteService.modifyProject(anyLong(), request);

        // then
        assertEquals(project.getRecruitJobs().size(), 1);
    }

    @Test
    @DisplayName("기존에 존재하던 모집직군 인원 수정")
    void modify_recruit_job_amount() {
        // given
        int modifyAmount = 1;
        Project project = ProjectFixture.withRecruitJobs(RecruitJobFixture.withRecruitAmount(Job.BACKEND_DEVELOPER, 5));
        when(projectRepository.findWithRecruitJobsById(anyLong()))
                .thenReturn(Optional.of(project));
        ProjectUpdateRequest request = ProjectUpdateRequest.builder()
                .recruitJobs(Set.of(new RecruitJobField(Job.BACKEND_DEVELOPER, modifyAmount))) // 기존에 존재하던 모집직군의 인원 수정 요청
                .build();

        // when
        projectWriteService.modifyProject(anyLong(), request);

        // then
        assertEquals(project.getRecruitJobs().get(0).getRecruitAmount(), modifyAmount);
    }
}