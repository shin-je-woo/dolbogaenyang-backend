package com.whatpl.domain.project.controller;

import com.whatpl.ApiDocTag;
import com.whatpl.BaseSecurityWebMvcTest;
import com.whatpl.global.common.model.Job;
import com.whatpl.global.security.model.WithMockWhatplMember;
import com.whatpl.global.common.model.ApplyStatus;
import com.whatpl.domain.project.dto.ApplyResponse;
import com.whatpl.domain.project.dto.ProjectApplyRequest;
import com.whatpl.domain.project.dto.ProjectApplyStatusRequest;
import com.whatpl.domain.project.model.ProjectApplyRequestFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.JsonFieldType;

import java.util.Map;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.resourceDetails;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
class ProjectApplyControllerTest extends BaseSecurityWebMvcTest {

    @Test
    @WithMockWhatplMember
    @DisplayName("프로젝트 지원 API Docs")
    void apply() throws Exception {
        // given
        ProjectApplyRequest request = ProjectApplyRequestFixture.apply(Job.BACKEND_DEVELOPER);
        Map<String, Object> map = Map.of(
                "applyJob", request.getApplyJob(),
                "content", request.getContent(),
                "applyType", request.getApplyType());
        String requestJson = objectMapper.writeValueAsString(map);
        long projectId = 1L;
        long applyId = 1L;
        long chatRoomId = 1L;
        ApplyResponse applyResponse = ApplyResponse.builder()
                .applyId(applyId)
                .projectId(projectId)
                .chatRoomId(chatRoomId)
                .build();
        when(projectApplyService.apply(any(ProjectApplyRequest.class), anyLong(), anyLong()))
                .thenReturn(applyResponse);

        // expected
        mockMvc.perform(post("/projects/{projectId}/apply", projectId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer {AccessToken}")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(requestJson)
                )
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.applyId").value(applyId),
                        jsonPath("$.projectId").value(projectId),
                        jsonPath("$.chatRoomId").value(chatRoomId)
                )
                .andDo(print())
                .andDo(document("project-apply",
                        resourceDetails().tag(ApiDocTag.PROJECT_APPLY.getTag())
                                .summary("프로젝트 지원 또는 참여 제안")
                                .description("""
                                        프로젝트에 지원 또는 참여 제안 API입니다.
                                        
                                        [API 요구사항]
                                        - 지원일 경우 요청 body의 applyType 값을 "APPLY"로 요청합니다.
                                        - 참여 제안일 경우 요청 body의 applyType 값을 "OFFER"로 요청합니다.
                                        - 요청 body의 applicantId는 참여 제안 요청에서 사용됩니다. 지원 요청에서는 사용되지 않습니다.
                                        
                                        [유효성 검증]
                                        - 삭제된 프로젝트는 지원(참여 제안) 불가
                                        - 모집완료된 프로젝트는 지원(참여 제안) 불가
                                        - 프로젝트 등록자는 본인이 등록한 프로젝트에 지원(참여 제안) 불가
                                        - 모집직군에 지원(참여 제안)하는 직무가 없을 경우, 모집직군에 지원하는 직무가 마감된 경우 지원(참여 제안) 불가
                                        - 이미 지원(참여 제안)한 프로젝트는 지원(참여 제안) 불가
                                        
                                        [권한]
                                        - 지원 - 인증된 모든 사용자
                                        - 참여 제안 - 프로젝트 등록자
                                        """),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("AccessToken")
                        ),
                        requestFields(
                                fieldWithPath("applyJob").type(JsonFieldType.STRING).description("지원 직무"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("지원 글"),
                                fieldWithPath("applyType").type(JsonFieldType.STRING).description("지원 타입")
                        ),
                        responseFields(
                                fieldWithPath("applyId").type(JsonFieldType.NUMBER).description("지원 ID"),
                                fieldWithPath("projectId").type(JsonFieldType.NUMBER).description("프로젝트 ID"),
                                fieldWithPath("chatRoomId").type(JsonFieldType.NUMBER).description("채팅방 ID")
                        )
                ));
    }

    @Test
    @WithMockWhatplMember
    @DisplayName("프로젝트 참여 제안 API Docs")
    void offer() throws Exception {
        // given
        ProjectApplyRequest request = ProjectApplyRequestFixture.offer();
        String requestJson = objectMapper.writeValueAsString(request);
        long projectId = 1L;
        long applyId = 1L;
        long chatRoomId = 1L;
        ApplyResponse applyResponse = ApplyResponse.builder()
                .applyId(applyId)
                .projectId(projectId)
                .chatRoomId(chatRoomId)
                .build();
        when(projectApplyService.apply(any(ProjectApplyRequest.class), anyLong(), anyLong()))
                .thenReturn(applyResponse);

        // expected
        mockMvc.perform(post("/projects/{projectId}/apply", projectId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer {AccessToken}")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(requestJson)
                )
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.applyId").value(applyId),
                        jsonPath("$.projectId").value(projectId),
                        jsonPath("$.chatRoomId").value(chatRoomId)
                )
                .andDo(print())
                .andDo(document("project-offer",
                        resourceDetails().tag(ApiDocTag.PROJECT_APPLY.getTag())
                                .summary("프로젝트 지원 또는 참여 제안")
                                .description("""
                                        프로젝트에 지원 또는 참여 제안 API입니다.
                                        
                                        [API 요구사항]
                                        - 지원일 경우 요청 body의 applyType 값을 "APPLY"로 요청합니다.
                                        - 참여 제안일 경우 요청 body의 applyType 값을 "OFFER"로 요청합니다.
                                        - 요청 body의 applicantId는 참여 제안 요청에서 사용됩니다. 지원 요청에서는 사용되지 않습니다.
                                        
                                        [유효성 검증]
                                        - 삭제된 프로젝트는 지원(참여 제안) 불가
                                        - 모집완료된 프로젝트는 지원(참여 제안) 불가
                                        - 프로젝트 등록자는 본인이 등록한 프로젝트에 지원(참여 제안) 불가
                                        - 모집직군에 지원(참여 제안)하는 직무가 없을 경우, 모집직군에 지원하는 직무가 마감된 경우 지원(참여 제안) 불가
                                        - 이미 지원(참여 제안)한 프로젝트는 지원(참여 제안) 불가
                                        
                                        [권한]
                                        - 지원 - 인증된 모든 사용자
                                        - 참여 제안 - 프로젝트 등록자
                                        """),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("AccessToken")
                        ),
                        requestFields(
                                fieldWithPath("applyJob").type(JsonFieldType.STRING).description("지원 직무"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("지원 글"),
                                fieldWithPath("applyType").type(JsonFieldType.STRING).description("지원 타입"),
                                fieldWithPath("applicantId").type(JsonFieldType.NUMBER).description("지원자 ID")
                        ),
                        responseFields(
                                fieldWithPath("applyId").type(JsonFieldType.NUMBER).description("지원 ID"),
                                fieldWithPath("projectId").type(JsonFieldType.NUMBER).description("프로젝트 ID"),
                                fieldWithPath("chatRoomId").type(JsonFieldType.NUMBER).description("채팅방 ID")
                        )
                ));
    }

    @Test
    @WithMockWhatplMember
    @DisplayName("프로젝트 지원서 승인/거절 API Docs")
    void applyStatus() throws Exception {
        // given
        long projectId = 1L;
        long applyId = 1L;
        ProjectApplyStatusRequest request = new ProjectApplyStatusRequest(ApplyStatus.ACCEPTED);
        String requestJson = objectMapper.writeValueAsString(request);
        doNothing().when(projectApplyService).status(anyLong(), anyLong(), any(ApplyStatus.class));

        // expected
        mockMvc.perform(patch("/projects/{projectId}/apply/{applyId}/status", projectId, applyId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer {AccessToken}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
                )
                .andExpectAll(
                        status().isNoContent()
                )
                .andDo(print())
                .andDo(document("update-project-apply-status",
                        resourceDetails().tag(ApiDocTag.PROJECT_APPLY.getTag())
                                .summary("프로젝트 지원 또는 참여 제안 승인/거절")
                                .description("""
                                        프로젝트 지원서를 승인/거절합니다.
                                        
                                        승인 대기 상태로는 변경할 수 없습니다. (승인/거절만 가능)
                                        
                                        [권한]
                                        - 지원일 경우 프로젝트 등록자
                                        - 참여제안일 경우 프로젝트 지원자
                                        """),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("AccessToken")
                        ),
                        pathParameters(
                                parameterWithName("projectId").description("프로젝트 ID"),
                                parameterWithName("applyId").description("지원서 ID")
                        ),
                        requestFields(
                                fieldWithPath("applyStatus").type(JsonFieldType.STRING).description("지원 상태")
                        )
                ));
    }

}