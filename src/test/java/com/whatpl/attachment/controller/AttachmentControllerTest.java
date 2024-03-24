package com.whatpl.attachment.controller;

import com.whatpl.BaseSecurityWebMvcTest;
import com.whatpl.attachment.dto.ResourceDto;
import org.hamcrest.core.AllOf;
import org.hamcrest.core.StringContains;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;
import static org.springframework.http.MediaType.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WithMockUser
class AttachmentControllerTest extends BaseSecurityWebMvcTest {

    @ParameterizedTest
    @MethodSource("successParameters")
    @DisplayName("첨부파일 업로드 시 Tika 라이브러리를 이용한 파일 검증이 성공하면 201 응답")
    void upload(String filename, String mimeType) throws Exception {
        // given
        MockMultipartFile multipartFile = createMockMultipartFile(filename, mimeType);

        // expected
        mockMvc.perform(multipart("/attachments")
                        .file(multipartFile))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @ParameterizedTest
    @MethodSource("failParameters")
    @DisplayName("첨부파일 업로드 시 Tika 라이브러리를 이용한 파일 검증이 실패하면 400 응답")
    void upload_fail(String filename, String mimeType) throws Exception {
        // given
        MockMultipartFile multipartFile = createMockMultipartFile(filename, mimeType);

        // expected
        mockMvc.perform(multipart("/attachments")
                        .file(multipartFile))
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.code").value("CONSTRAINT"),
                        jsonPath("$.message").value("허용된 확장자가 아닙니다. [jpg, jpeg, png, gif, pdf]"),
                        jsonPath("$.status").value(HttpStatus.valueOf(400).name())
                );
    }

    @ParameterizedTest
    @MethodSource("successParameters")
    @DisplayName("첨부파일 다운로드 요청 시 Content-Disposition 헤더에 attachment, filename 을 포함하여 응답")
    void download(String filename, String mimeType) throws Exception {
        // given
        long attachmentId = 1L;
        InputStreamResource resource = new InputStreamResource(createMockMultipartFile(filename, mimeType).getInputStream());
        ResourceDto resourceDto = ResourceDto.builder()
                .mimeType(mimeType)
                .resource(resource)
                .fileName(filename)
                .build();
        when(attachmentService.download(attachmentId)).thenReturn(resourceDto);

        // expected
        mockMvc.perform(get("/attachments/{id}", attachmentId))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType(APPLICATION_OCTET_STREAM),
                        header().string(CONTENT_DISPOSITION, AllOf.allOf(
                                StringContains.containsString("attachment;"),
                                StringContains.containsString("filename="),
                                StringContains.containsString(filename)
                        )));
    }

    @ParameterizedTest
    @MethodSource("successParameters")
    @DisplayName("첨부파일 미리보기 요청 시 Content-Type 은 저장된 파일의 MIME 타입으로 응답")
    void preview(String filename, String mimeType) throws Exception {
        // given
        long attachmentId = 1L;
        InputStreamResource resource = new InputStreamResource(createMockMultipartFile(filename, mimeType).getInputStream());
        ResourceDto resourceDto = ResourceDto.builder()
                .mimeType(mimeType)
                .resource(resource)
                .fileName(filename)
                .build();
        when(attachmentService.download(attachmentId)).thenReturn(resourceDto);

        // expected
        mockMvc.perform(get("/attachments/{id}/images", attachmentId))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType(mimeType));
    }

    private MockMultipartFile createMockMultipartFile(String filename, String mimeType) throws IOException {
        String path = requireNonNull(getClass().getClassLoader().getResource("files/" + filename)).getPath();
        InputStream inputStream = new FileInputStream(path);
        return new MockMultipartFile("file", filename, mimeType, inputStream);
    }

    static Stream<Arguments> successParameters() {
        return Stream.of(
                Arguments.of("cat.jpg", IMAGE_JPEG_VALUE),
                Arguments.of("minions.png", IMAGE_PNG_VALUE),
                Arguments.of("dummy.pdf", APPLICATION_PDF_VALUE)
        );
    }

    static Stream<Arguments> failParameters() {
        return Stream.of(
                Arguments.of("fail.docx", "docx"),
                Arguments.of("fail.pptx", "pptx")
        );
    }
}