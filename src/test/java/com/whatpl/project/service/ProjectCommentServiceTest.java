package com.whatpl.project.service;

import com.whatpl.global.exception.BizException;
import com.whatpl.global.exception.ErrorCode;
import com.whatpl.member.domain.Member;
import com.whatpl.member.model.MemberFixture;
import com.whatpl.project.domain.Project;
import com.whatpl.project.domain.ProjectComment;
import com.whatpl.project.dto.ProjectCommentListResponse;
import com.whatpl.project.dto.ProjectCommentUpdateRequest;
import com.whatpl.project.model.ProjectCommentFixture;
import com.whatpl.project.model.ProjectFixture;
import com.whatpl.project.repository.ProjectCommentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectCommentServiceTest {

    @InjectMocks
    ProjectCommentService projectCommentService;

    @Mock
    ProjectCommentRepository projectCommentRepository;

    @Test
    @DisplayName("댓글 수정 시 요청 projectId와 댓글의 projectId가 일치하지 않을 경우 예외 발생")
    void updateProjectComment_not_match() {
        // given
        Project project = spy(ProjectFixture.create());
        when(project.getId()).thenReturn(1L);
        ProjectComment projectComment = ProjectCommentFixture.normal(project, MemberFixture.onlyRequired());
        when(projectCommentRepository.findById(anyLong())).thenReturn(Optional.of(projectComment));
        ProjectCommentUpdateRequest request = new ProjectCommentUpdateRequest("수정 댓글");

        // when & then
        BizException bizException = assertThrows(BizException.class, () -> projectCommentService.updateProjectComment(request, 2L, anyLong()));
        assertEquals(ErrorCode.REQUEST_VALUE_INVALID, bizException.getErrorCode());
    }

    @Test
    @DisplayName("댓글 삭제 시 요청 projectId와 댓글의 projectId가 일치하지 않을 경우 예외 발생")
    void deleteProjectComment_not_match() {
        // given
        Project project = spy(ProjectFixture.create());
        when(project.getId()).thenReturn(1L);
        ProjectComment projectComment = ProjectCommentFixture.normal(project, MemberFixture.onlyRequired());
        when(projectCommentRepository.findById(anyLong())).thenReturn(Optional.of(projectComment));

        // when & then
        BizException bizException = assertThrows(BizException.class, () -> projectCommentService.deleteProjectComment(2L, anyLong()));
        assertEquals(ErrorCode.REQUEST_VALUE_INVALID, bizException.getErrorCode());
    }

    @Test
    @DisplayName("댓글 리스트 조회 시 삭제된 댓글은 '삭제된 댓글입니다.'로 표기")
    void readProjectCommentList_deleted_comment() {
        // given
        Project project = ProjectFixture.create();
        Member writer = spy(MemberFixture.onlyRequired());
        ProjectComment projectComment = spy(ProjectCommentFixture.deleted(project, writer));
        when(writer.getId()).thenReturn(1L);
        when(projectComment.getId()).thenReturn(1L);
        when(projectCommentRepository.findWithAllByProjectId(Mockito.anyLong()))
                .thenReturn(List.of(projectComment));

        // when
        ProjectCommentListResponse projectCommentListResponse = projectCommentService.readProjectCommentList(anyLong());

        // then
        String resultContent = projectCommentListResponse.getComments().get(0).getContent();
        assertNotEquals(projectComment.getContent(), resultContent);
        assertEquals("삭제된 댓글입니다.", resultContent);
    }
}