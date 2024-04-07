package com.whatpl.project.controller;

import com.whatpl.ApiDocTag;
import com.whatpl.BaseSecurityWebMvcTest;
import com.whatpl.global.security.model.WithMockWhatplMember;
import com.whatpl.project.dto.ProjectCreateRequest;
import com.whatpl.project.model.ProjectCreateRequestFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.JsonFieldType;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.resourceDetails;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
class ProjectControllerTest extends BaseSecurityWebMvcTest {

    @Test
    @WithMockWhatplMember
    @DisplayName("프로젝트 등록 API Docs")
    void write() throws Exception {
        // given
        ProjectCreateRequest projectCreateRequest = ProjectCreateRequestFixture.create();
        String requestJson = objectMapper.writeValueAsString(projectCreateRequest);
        Long createdProjectId = 1L;
        when(projectWriteService.createProject(any(ProjectCreateRequest.class), anyLong()))
                .thenReturn(createdProjectId);

        // expected
        mockMvc.perform(post("/projects")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer {AccessToken}")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(requestJson)
                ).andExpectAll(
                        status().isCreated(),
                        header().exists(HttpHeaders.LOCATION),
                        header().string(HttpHeaders.LOCATION, String.format("/projects/%d", createdProjectId))
                )
                .andDo(print())
                .andDo(document("create-project",
                        resourceDetails().tag(ApiDocTag.PROJECT.getTag())
                                .summary("프로젝트 등록"),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("AccessToken")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.LOCATION).description("등록한 프로젝트의 URI 경로")
                        ),
                        requestFields(
                                fieldWithPath("title").type(JsonFieldType.STRING).description("제목"),
                                fieldWithPath("subject").type(JsonFieldType.STRING).description("도메인(관심주제)"),
                                fieldWithPath("recruitJobs").type(JsonFieldType.ARRAY).description("모집직군"),
                                fieldWithPath("recruitJobs[].job").type(JsonFieldType.STRING).description("직무"),
                                fieldWithPath("recruitJobs[].totalCount").type(JsonFieldType.NUMBER).description("모집인원"),
                                fieldWithPath("skills").type(JsonFieldType.ARRAY).description("기술스택"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("프로젝트 설명"),
                                fieldWithPath("profitable").type(JsonFieldType.BOOLEAN).description("수익화 여부"),
                                fieldWithPath("meetingType").type(JsonFieldType.STRING).description("모임방식"),
                                fieldWithPath("startDate").type(JsonFieldType.STRING).description("프로젝트 진행 기간 - 시작일자 yyyy-MM-dd"),
                                fieldWithPath("endDate").type(JsonFieldType.STRING).description("프로젝트 진행 기간 - 종료일자 yyyy-MM-dd"),
                                fieldWithPath("representId").type(JsonFieldType.NUMBER).description("대표이미지 ID").optional(),
                                fieldWithPath("wishCareer").type(JsonFieldType.STRING).description("희망 연차").optional(),
                                fieldWithPath("wishCareerUpDown").type(JsonFieldType.STRING).description("희망 연차 이상/이하").optional(),
                                fieldWithPath("wishWorkTime").type(JsonFieldType.STRING).description("희망 주당 작업 시간").optional()
                        )
                ));
    }
}