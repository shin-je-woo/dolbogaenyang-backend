package com.whatpl.attachment.docs;

import com.whatpl.ApiDocTag;
import com.whatpl.BaseSecurityWebMvcTest;
import com.whatpl.attachment.dto.ResourceDto;
import com.whatpl.global.security.model.WithMockWhatplMember;
import org.hamcrest.core.AllOf;
import org.hamcrest.core.StringContains;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.security.test.context.support.WithMockUser;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.resourceDetails;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.*;
import static org.springframework.http.MediaType.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.multipart;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
public class AttachmentDocsTest extends BaseSecurityWebMvcTest {

    @Test
    @WithMockWhatplMember
    @DisplayName("첨부파일 업로드 API Docs")
    void upload() throws Exception {
        // given
        String filename = "cat.jpg";
        MockMultipartFile multipartFile = createMockMultipartFile("file", filename, IMAGE_JPEG_VALUE);

        // expected
        mockMvc.perform(multipart("/attachments")
                        .file(multipartFile)
                        .header(AUTHORIZATION, "Bearer {AccessToken}")
                        .contentType(MULTIPART_FORM_DATA))
                .andDo(print())
                .andExpect(status().isCreated())
                .andDo(document("create-attachments",
                        resourceDetails().tag(ApiDocTag.ATTACHMENT.getTag())
                                .summary("첨부파일 업로드")
                                .description("""
                                        첨부파일 업로드
                                                                                
                                        현재 사용 플러그인(ePages/restdocs-api-spec)이 multipart/form-data 문서화가 지원되지 않습니다. (try it out 불가능)

                                        아래 형식을 참고하여 요청하면 됩니다.

                                        | name   | filename  |Content-Type                 |
                                        |--------|-----------|-----------------------------|
                                        | file   | 파일명     | MimeType   (ex. image/png)  |
                                        """),
                        requestHeaders(
                                headerWithName(AUTHORIZATION).description("AccessToken"),
                                headerWithName(CONTENT_TYPE).description(MULTIPART_FORM_DATA_VALUE)
                        ),
                        requestParts(
                                partWithName("file").description("업로드 파일")
                        ),
                        responseFields(
                                fieldWithPath("attachmentId").description("업로드된 파일 id")
                        )
                ));
    }

    @Test
    @WithMockUser
    @DisplayName("첨부파일 업로드 API Docs - 허용된 확장자 X")
    void upload_fail() throws Exception {
        // given
        MockMultipartFile multipartFile = createMockMultipartFile("file", "fail.docx", "application/docx");

        // expected
        mockMvc.perform(multipart("/attachments")
                        .file(multipartFile)
                        .header(AUTHORIZATION, "Bearer {AccessToken}")
                        .contentType(MULTIPART_FORM_DATA))
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.code").value("CONSTRAINT"),
                        jsonPath("$.message").value("허용된 확장자가 아닙니다. [jpg, jpeg, png, gif, pdf]"),
                        jsonPath("$.status").value(HttpStatus.valueOf(400).name())
                )
                .andDo(print())
                .andDo(document("create-attachments-fail",
                        resourceDetails().tag(ApiDocTag.ATTACHMENT.getTag()),
                        requestHeaders(
                                headerWithName(AUTHORIZATION).description("AccessToken"),
                                headerWithName(CONTENT_TYPE).description(MULTIPART_FORM_DATA_VALUE)
                        ),
                        requestParts(
                                partWithName("file").description("업로드 파일")
                        ),
                        responseFields(
                                fieldWithPath("code").description("CONSTRAINT"),
                                fieldWithPath("message").description("허용된 확장자가 아닙니다. [jpg, jpeg, png, gif, pdf]"),
                                fieldWithPath("status").description(HttpStatus.BAD_REQUEST.name())
                        )
                ));
    }

    @Test
    @DisplayName("첨부파일 다운로드 API Docs")
    void download() throws Exception {
        // given
        long attachmentId = 1L;
        String filename = "cat.jpg";
        String mimeType = IMAGE_JPEG_VALUE;
        InputStreamResource resource = new InputStreamResource(createMockMultipartFile("file", filename, mimeType).getInputStream());
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
                        )))
                .andDo(document("download-attachments",
                        resourceDetails().tag(ApiDocTag.ATTACHMENT.getTag())
                                .summary("첨부파일 다운로드"),
                        pathParameters(
                                parameterWithName("id").description("첨부파일 id")
                        )
                ));
    }

    @Test
    @DisplayName("첨부파일 미리보기 API Docs")
    void preview() throws Exception {
        // given
        long attachmentId = 1L;
        String filename = "cat.jpg";
        String mimeType = IMAGE_JPEG_VALUE;
        InputStreamResource resource = new InputStreamResource(createMockMultipartFile("file", filename, mimeType).getInputStream());
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
                        content().contentType(mimeType))
                .andDo(document("preview-attachments",
                        resourceDetails().tag(ApiDocTag.ATTACHMENT.getTag())
                                .summary("첨부파일 미리보기 (이미지, pdf)"),
                        pathParameters(
                                parameterWithName("id").description("첨부파일 id")
                        )));
    }
}
