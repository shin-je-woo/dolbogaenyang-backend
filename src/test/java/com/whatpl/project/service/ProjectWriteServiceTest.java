package com.whatpl.project.service;

import com.whatpl.attachment.domain.Attachment;
import com.whatpl.attachment.repository.AttachmentRepository;
import com.whatpl.global.exception.BizException;
import com.whatpl.global.exception.ErrorCode;
import com.whatpl.member.model.MemberFixture;
import com.whatpl.member.repository.MemberRepository;
import com.whatpl.project.dto.ProjectCreateRequest;
import com.whatpl.project.model.ProjectCreateRequestFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;

import java.util.Optional;

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
}