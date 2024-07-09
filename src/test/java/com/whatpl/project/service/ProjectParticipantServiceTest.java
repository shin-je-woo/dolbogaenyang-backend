package com.whatpl.project.service;

import com.whatpl.global.common.domain.enums.Job;
import com.whatpl.member.domain.Member;
import com.whatpl.member.model.MemberFixture;
import com.whatpl.project.domain.Apply;
import com.whatpl.project.domain.Project;
import com.whatpl.project.domain.ProjectParticipant;
import com.whatpl.global.common.domain.enums.ApplyStatus;
import com.whatpl.project.model.ApplyFixture;
import com.whatpl.project.model.ProjectFixture;
import com.whatpl.project.repository.ApplyRepository;
import com.whatpl.project.repository.ProjectParticipantRepository;
import com.whatpl.project.repository.ProjectRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectParticipantServiceTest {

    @InjectMocks
    ProjectParticipantService projectParticipantService;

    @Mock
    ProjectRepository projectRepository;

    @Mock
    ProjectParticipantRepository projectParticipantRepository;

    @Mock
    ApplyRepository applyRepository;

    @Test
    @DisplayName("프로젝트 참여자 삭제 시 참여자가 삭제되고, Apply 가 제외상태로 변경된다.")
    void deleteParticipant() {
        // given
        Member participant = Mockito.spy(MemberFixture.onlyRequired());
        Project project = Mockito.spy(ProjectFixture.create());
        ProjectParticipant projectParticipant = ProjectParticipant.builder()
                .job(Job.BACKEND_DEVELOPER)
                .project(project)
                .participant(participant)
                .build();
        Apply apply = ApplyFixture.accepted(projectParticipant.getJob(), participant, project);
        when(participant.getId()).thenReturn(1L);
        when(project.getId()).thenReturn(1L);
        when(projectRepository.findById(anyLong())).thenReturn(Optional.of(project));
        when(projectParticipantRepository.findWithAllById(anyLong())).thenReturn(Optional.of(projectParticipant));
        when(applyRepository.findByApplicantAndStatus(projectParticipant.getParticipant(), ApplyStatus.ACCEPTED))
                .thenReturn(Optional.of(apply));

        // when
        projectParticipantService.deleteParticipant(project.getId(), participant.getId());

        // then
        verify(projectParticipantRepository, times(1)).delete(projectParticipant);
        assertEquals(ApplyStatus.EXCLUDED, apply.getStatus());
    }
}