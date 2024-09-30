package com.whatpl.domain.attachment.service;

import com.whatpl.domain.attachment.domain.Attachment;
import com.whatpl.domain.attachment.dto.ResourceDto;
import com.whatpl.domain.attachment.repository.AttachmentRepository;
import com.whatpl.external.upload.FileUploader;
import io.awspring.cloud.s3.S3Resource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.util.Optional;
import java.util.UUID;

import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.IMAGE_PNG_VALUE;

@ExtendWith(MockitoExtension.class)
class AttachmentServiceTest {

    @InjectMocks
    AttachmentService attachmentService;

    @Mock
    FileUploader fileUploader;

    @Mock
    AttachmentRepository attachmentRepository;

    @Test
    @DisplayName("업로드 시 FileUploader 를 정상 호출하고, repository 에 파일정보를 저장한다.")
    void upload() {
        // given
        MockMultipartFile multipartFile = new MockMultipartFile("file", "content".getBytes());
        when(fileUploader.upload(any()))
                .thenReturn(UUID.randomUUID().toString());
        when(attachmentRepository.save(any()))
                .thenReturn(mock(Attachment.class));

        //when
        attachmentService.uploadAndSave(multipartFile);

        //then
        verify(fileUploader, times(1)).upload(multipartFile);
        verify(attachmentRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("다운로드 시 FileUploader 를 정상 호출하고, repository 에서 파일정보를 가져온다.")
    void download() {
        // given
        Attachment attachment = Attachment.builder()
                .fileName("image.png")
                .storedName(UUID.randomUUID().toString())
                .mimeType(IMAGE_PNG_VALUE)
                .build();
        S3Resource s3Resource = mock(S3Resource.class);
        when(attachmentRepository.findById(any())).thenReturn(Optional.ofNullable(attachment));
        when(fileUploader.download(requireNonNull(attachment).getStoredName())).thenReturn(s3Resource);

        // when
        ResourceDto resourceDto = attachmentService.download(any());

        // then
        verify(fileUploader, times(1)).download(attachment.getStoredName());
        verify(attachmentRepository, times(1)).findById(any());
        assertEquals(attachment.getFileName(), resourceDto.getFileName());
        assertEquals(attachment.getMimeType(), resourceDto.getMimeType());
        assertEquals(s3Resource, resourceDto.getResource());
    }
}