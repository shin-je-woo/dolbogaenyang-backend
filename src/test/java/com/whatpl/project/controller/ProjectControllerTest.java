package com.whatpl.project.controller;

import com.whatpl.ApiDocTag;
import com.whatpl.BaseSecurityWebMvcTest;
import com.whatpl.global.security.model.WithMockWhatplMember;
import com.whatpl.project.dto.ProjectCreateRequest;
import com.whatpl.project.dto.ProjectReadResponse;
import com.whatpl.project.model.ProjectCreateRequestFixture;
import com.whatpl.project.model.ProjectReadResponseFixture;
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
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
                        .content(requestJson))
                .andExpectAll(
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
                                fieldWithPath("recruitJobs[].recruitAmount").type(JsonFieldType.NUMBER).description("모집 인원수"),
                                fieldWithPath("skills").type(JsonFieldType.ARRAY).description("기술스택"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("프로젝트 설명"),
                                fieldWithPath("profitable").type(JsonFieldType.BOOLEAN).description("수익화 여부"),
                                fieldWithPath("meetingType").type(JsonFieldType.STRING).description("모임방식"),
                                fieldWithPath("startDate").type(JsonFieldType.STRING).description("프로젝트 진행 기간 - 시작일자 yyyy-MM-dd"),
                                fieldWithPath("endDate").type(JsonFieldType.STRING).description("프로젝트 진행 기간 - 종료일자 yyyy-MM-dd"),
                                fieldWithPath("representImageId").type(JsonFieldType.NUMBER).description("대표이미지 ID").optional()
                        )
                ));
    }

    @Test
    @WithMockWhatplMember
    @DisplayName("프로젝트 조회 API Docs")
    void read() throws Exception {
        // given
        long projectId = 1L;
        ProjectReadResponse response = ProjectReadResponseFixture.from(projectId);
        when(projectReadService.readProject(projectId))
                .thenReturn(response);

        // expected
        mockMvc.perform(get("/projects/{projectId}", projectId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer {AccessToken}"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.projectId").value(response.getProjectId()),
                        jsonPath("$.title").value(response.getTitle()),
                        jsonPath("$.projectStatus").value(response.getProjectStatus().getValue()),
                        jsonPath("$.meetingType").value(response.getMeetingType().getValue()),
                        jsonPath("$.views").value(response.getViews()),
                        jsonPath("$.likes").value(response.getLikes()),
                        jsonPath("$.profitable").value(response.isProfitable()),
                        jsonPath("$.createdAt").value(response.getCreatedAt().toString()),
                        jsonPath("$.content").value(response.getContent()),
                        jsonPath("$.subject").value(response.getSubject().getValue()),
                        jsonPath("$.skills[0]").value(response.getSkills().get(0).getValue()),
                        jsonPath("$.skills[1]").value(response.getSkills().get(1).getValue()),
                        jsonPath("$.startDate").value(response.getStartDate().toString()),
                        jsonPath("$.endDate").value(response.getEndDate().toString()),
                        jsonPath("$.projectJobParticipants[0].job").value(response.getProjectJobParticipants().get(0).getJob().getValue()),
                        jsonPath("$.projectJobParticipants[0].recruitAmount").value(response.getProjectJobParticipants().get(0).getRecruitAmount()),
                        jsonPath("$.projectJobParticipants[0].participantAmount").value(response.getProjectJobParticipants().get(0).getParticipantAmount()),
                        jsonPath("$.projectJobParticipants[0].participants[0].memberId")
                                .value(response.getProjectJobParticipants().get(0).getParticipants().get(0).getMemberId()),
                        jsonPath("$.projectJobParticipants[0].participants[0].nickname")
                                .value(response.getProjectJobParticipants().get(0).getParticipants().get(0).getNickname()),
                        jsonPath("$.projectJobParticipants[0].participants[0].career")
                                .value(response.getProjectJobParticipants().get(0).getParticipants().get(0).getCareer().getValue()),
                        jsonPath("$.projectJobParticipants[0].participants[1].memberId")
                                .value(response.getProjectJobParticipants().get(0).getParticipants().get(1).getMemberId()),
                        jsonPath("$.projectJobParticipants[0].participants[1].nickname")
                                .value(response.getProjectJobParticipants().get(0).getParticipants().get(1).getNickname()),
                        jsonPath("$.projectJobParticipants[0].participants[1].career")
                                .value(response.getProjectJobParticipants().get(0).getParticipants().get(1).getCareer().getValue()),
                        jsonPath("$.projectJobParticipants[1].job").value(response.getProjectJobParticipants().get(1).getJob().getValue()),
                        jsonPath("$.projectJobParticipants[1].recruitAmount").value(response.getProjectJobParticipants().get(1).getRecruitAmount()),
                        jsonPath("$.projectJobParticipants[1].participantAmount").value(response.getProjectJobParticipants().get(1).getParticipantAmount())
                )
                .andDo(print())
                .andDo(document("read-project",
                        resourceDetails().tag(ApiDocTag.PROJECT.getTag())
                                .summary("프로젝트 조회"),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("AccessToken")
                        ),
                        pathParameters(
                                parameterWithName("projectId").description("프로젝트 ID")
                        ),
                        responseFields(
                                fieldWithPath("projectId").type(JsonFieldType.NUMBER).description("프로젝트 ID"),
                                fieldWithPath("title").type(JsonFieldType.STRING).description("프로젝트 제목"),
                                fieldWithPath("projectStatus").type(JsonFieldType.STRING).description("프로젝트 상태"),
                                fieldWithPath("meetingType").type(JsonFieldType.STRING).description("모임 방식"),
                                fieldWithPath("views").type(JsonFieldType.NUMBER).description("조회수"),
                                fieldWithPath("likes").type(JsonFieldType.NUMBER).description("좋아요 수"),
                                fieldWithPath("writerNickname").type(JsonFieldType.STRING).description("작성자 닉네임"),
                                fieldWithPath("createdAt").type(JsonFieldType.STRING).description("작성 일시"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("본문"),
                                fieldWithPath("profitable").type(JsonFieldType.BOOLEAN).description("수익화 여부"),
                                fieldWithPath("subject").type(JsonFieldType.STRING).description("도메인(관심 주제)"),
                                fieldWithPath("skills").type(JsonFieldType.ARRAY).description("사용 기술 스택"),
                                fieldWithPath("startDate").type(JsonFieldType.STRING).description("시작 일자"),
                                fieldWithPath("endDate").type(JsonFieldType.STRING).description("종료 일자"),
                                fieldWithPath("projectJobParticipants").type(JsonFieldType.ARRAY).description("직무별 참여자 (팀원 구성)"),
                                fieldWithPath("projectJobParticipants[].job").type(JsonFieldType.STRING).description("직무"),
                                fieldWithPath("projectJobParticipants[].recruitAmount").type(JsonFieldType.NUMBER).description("모집 인원수"),
                                fieldWithPath("projectJobParticipants[].participantAmount").type(JsonFieldType.NUMBER).description("참여자 인원수"),
                                fieldWithPath("projectJobParticipants[].participants").type(JsonFieldType.ARRAY).description("참여자"),
                                fieldWithPath("projectJobParticipants[].participants[].memberId").type(JsonFieldType.NUMBER).description("참여자 ID"),
                                fieldWithPath("projectJobParticipants[].participants[].nickname").type(JsonFieldType.STRING).description("닉네임"),
                                fieldWithPath("projectJobParticipants[].participants[].career").type(JsonFieldType.STRING).description("경력")
                        )
                ));
    }
}