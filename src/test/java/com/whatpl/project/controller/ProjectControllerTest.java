package com.whatpl.project.controller;

import com.whatpl.ApiDocTag;
import com.whatpl.BaseSecurityWebMvcTest;
import com.whatpl.global.common.domain.enums.Job;
import com.whatpl.global.security.model.WithMockWhatplMember;
import com.whatpl.project.domain.enums.ApplyStatus;
import com.whatpl.project.dto.*;
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

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.resourceDetails;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
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
                                fieldWithPath("subjects").type(JsonFieldType.ARRAY).description("도메인(관심주제)"),
                                fieldWithPath("recruitJobs").type(JsonFieldType.ARRAY).description("모집직군"),
                                fieldWithPath("recruitJobs[].job").type(JsonFieldType.STRING).description("직무"),
                                fieldWithPath("recruitJobs[].totalCount").type(JsonFieldType.NUMBER).description("모집인원"),
                                fieldWithPath("skills").type(JsonFieldType.ARRAY).description("기술스택"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("프로젝트 설명"),
                                fieldWithPath("profitable").type(JsonFieldType.BOOLEAN).description("수익화 여부"),
                                fieldWithPath("meetingType").type(JsonFieldType.STRING).description("모임방식"),
                                fieldWithPath("startDate").type(JsonFieldType.STRING).description("프로젝트 진행 기간 - 시작일자 yyyy-MM-dd"),
                                fieldWithPath("endDate").type(JsonFieldType.STRING).description("프로젝트 진행 기간 - 종료일자 yyyy-MM-dd"),
                                fieldWithPath("representId").type(JsonFieldType.NUMBER).description("대표이미지 ID").optional()
                        )
                ));
    }

    @Test
    @WithMockWhatplMember
    @DisplayName("프로젝트 지원 API Docs")
    void apply() throws Exception {
        // given
        ProjectApplyRequest request = new ProjectApplyRequest(Job.BACKEND_DEVELOPER, "<p>테스트 콘텐츠 HTML<p>");
        String requestJson = objectMapper.writeValueAsString(request);
        Long projectId = 1L;
        Long applyId = 1L;
        when(projectApplyService.apply(any(ProjectApplyRequest.class), anyLong(), anyLong()))
                .thenReturn(applyId);

        // expected
        mockMvc.perform(post("/projects/{projectId}/applications", projectId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer {AccessToken}")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(requestJson)
                )
                .andExpectAll(
                        status().isCreated(),
                        header().exists(HttpHeaders.LOCATION),
                        header().string(HttpHeaders.LOCATION, String.format("/projects/%d/applications/%d", projectId, applyId))
                )
                .andDo(print())
                .andDo(document("create-project-apply",
                        resourceDetails().tag(ApiDocTag.PROJECT.getTag())
                                .summary("프로젝트 지원")
                                .description("""
                                        프로젝트에 지원합니다.
                                                                                
                                        삭제된 프로젝트는 지원 불가
                                                                                
                                        모집완료된 프로젝트는 지원 불가
                                                                                
                                        프로젝트 등록자는 본인이 등록한 프로젝트에 지원 불가
                                                                                
                                        모집직군에 지원하는 직무가 없을 경우, 모집직군에 지원하는 직무가 마감된 경우 지원 불가
                                                                                
                                        이미 지원한 프로젝트는 지원 불가
                                        """),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("AccessToken")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.LOCATION).description("등록한 프로젝트 지원서의 URI 경로")
                        ),
                        requestFields(
                                fieldWithPath("applyJob").type(JsonFieldType.STRING).description("지원 직무"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("지원 글")
                        )
                ));
    }

    @Test
    @WithMockWhatplMember
    @DisplayName("프로젝트 지원서 조회 API Docs")
    void applyRead() throws Exception {
        // given
        long projectId = 1L;
        long applyId = 1L;
        LocalDateTime recruiterReadAt = LocalDateTime.now(Clock.fixed(
                Instant.parse("2024-04-12T12:35:43.00Z"),
                ZoneId.of("Asia/Seoul"))
        );
        ProjectApplyReadResponse response = ProjectApplyReadResponse.builder()
                .projectId(projectId)
                .applyId(applyId)
                .applicantId(1L)
                .applicantNickname("왓플테스트유저1")
                .status(ApplyStatus.WAITING)
                .job(Job.BACKEND_DEVELOPER)
                .content("지원서 내용")
                .recruiterReadAt(recruiterReadAt)
                .build();
        when(projectApplyService.read(projectId, applyId))
                .thenReturn(response);

        // expected
        mockMvc.perform(get("/projects/{projectId}/applications/{applyId}", projectId, applyId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer {AccessToken}")
                )
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.projectId").value(response.getProjectId()),
                        jsonPath("$.applyId").value(response.getApplyId()),
                        jsonPath("$.applicantId").value(response.getApplicantId()),
                        jsonPath("$.applicantNickname").value(response.getApplicantNickname()),
                        jsonPath("$.job").value(response.getJob().getValue()),
                        jsonPath("$.status").value(response.getStatus().getValue()),
                        jsonPath("$.content").value(response.getContent()),
                        jsonPath("$.recruiterReadAt").value(response.getRecruiterReadAt().toString())
                )
                .andDo(print())
                .andDo(document("read-project-apply",
                        resourceDetails().tag(ApiDocTag.PROJECT.getTag())
                                .summary("프로젝트 지원서 조회")
                                .description("""
                                        프로젝트 지원서를 조회합니다.
                                                                                
                                        프로젝트의 지원자, 모집자만 조회 가능합니다.
                                        """),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("AccessToken")
                        ),
                        pathParameters(
                                parameterWithName("projectId").description("프로젝트 ID"),
                                parameterWithName("applyId").description("지원서 ID")
                        ),
                        responseFields(
                                fieldWithPath("projectId").type(JsonFieldType.NUMBER).description("프로젝트 ID"),
                                fieldWithPath("applyId").type(JsonFieldType.NUMBER).description("지원서 ID"),
                                fieldWithPath("applicantId").type(JsonFieldType.NUMBER).description("지원자 ID"),
                                fieldWithPath("applicantNickname").type(JsonFieldType.STRING).description("지원자 닉네임"),
                                fieldWithPath("job").type(JsonFieldType.STRING).description("지원 직무"),
                                fieldWithPath("status").type(JsonFieldType.STRING).description("지원 상태"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("지원 내용"),
                                fieldWithPath("recruiterReadAt").type(JsonFieldType.STRING).description("모집자 조회 시간")
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
        mockMvc.perform(patch("/projects/{projectId}/applications/{applyId}/status", projectId, applyId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer {AccessToken}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
                )
                .andExpectAll(
                        status().isNoContent()
                )
                .andDo(print())
                .andDo(document("update-project-apply-status",
                        resourceDetails().tag(ApiDocTag.PROJECT.getTag())
                                .summary("프로젝트 지원서 승인/거절")
                                .description("""
                                        프로젝트 지원서를 승인/거절합니다.
                                                                                
                                        프로젝트의 모집자만 승인/거절 가능합니다.
                                                                                
                                        승인 대기 상태로는 변경할 수 없습니다. (승인/거절만 가능)
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
                        jsonPath("$.subjects[0]").value(response.getSubjects().get(0).getValue()),
                        jsonPath("$.subjects[1]").value(response.getSubjects().get(1).getValue()),
                        jsonPath("$.skills[0]").value(response.getSkills().get(0).getValue()),
                        jsonPath("$.skills[1]").value(response.getSkills().get(1).getValue()),
                        jsonPath("$.startDate").value(response.getStartDate().toString()),
                        jsonPath("$.endDate").value(response.getEndDate().toString()),
                        jsonPath("$.projectJobParticipants[0].job").value(response.getProjectJobParticipants().get(0).getJob().getValue()),
                        jsonPath("$.projectJobParticipants[0].totalAmount").value(response.getProjectJobParticipants().get(0).getTotalAmount()),
                        jsonPath("$.projectJobParticipants[0].currentAmount").value(response.getProjectJobParticipants().get(0).getCurrentAmount()),
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
                        jsonPath("$.projectJobParticipants[1].totalAmount").value(response.getProjectJobParticipants().get(1).getTotalAmount()),
                        jsonPath("$.projectJobParticipants[1].currentAmount").value(response.getProjectJobParticipants().get(1).getCurrentAmount())
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
                                fieldWithPath("subjects").type(JsonFieldType.ARRAY).description("도메인(관심 주제)"),
                                fieldWithPath("skills").type(JsonFieldType.ARRAY).description("사용 기술 스택"),
                                fieldWithPath("startDate").type(JsonFieldType.STRING).description("시작 일자"),
                                fieldWithPath("endDate").type(JsonFieldType.STRING).description("종료 일자"),
                                fieldWithPath("projectJobParticipants").type(JsonFieldType.ARRAY).description("직무별 참여자 (팀원 구성)"),
                                fieldWithPath("projectJobParticipants[].job").type(JsonFieldType.STRING).description("직무"),
                                fieldWithPath("projectJobParticipants[].totalAmount").type(JsonFieldType.NUMBER).description("모집 인원"),
                                fieldWithPath("projectJobParticipants[].currentAmount").type(JsonFieldType.NUMBER).description("현재 인원"),
                                fieldWithPath("projectJobParticipants[].participants").type(JsonFieldType.ARRAY).description("참여자"),
                                fieldWithPath("projectJobParticipants[].participants[].memberId").type(JsonFieldType.NUMBER).description("참여자 ID"),
                                fieldWithPath("projectJobParticipants[].participants[].nickname").type(JsonFieldType.STRING).description("닉네임"),
                                fieldWithPath("projectJobParticipants[].participants[].career").type(JsonFieldType.STRING).description("경력")
                        )
                ));
    }
}