package com.whatpl.domain.project.controller;

import com.whatpl.ApiDocTag;
import com.whatpl.BaseSecurityWebMvcTest;
import com.whatpl.domain.project.dto.*;
import com.whatpl.global.common.domain.enums.Job;
import com.whatpl.global.common.domain.enums.Skill;
import com.whatpl.global.common.domain.enums.Subject;
import com.whatpl.global.security.model.WithMockWhatplMember;
import com.whatpl.domain.project.domain.enums.ProjectStatus;
import com.whatpl.domain.project.model.ProjectCreateRequestFixture;
import com.whatpl.domain.project.model.ProjectReadResponseFixture;
import com.whatpl.domain.project.model.ProjectUpdateRequestFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.JsonFieldType;

import java.util.List;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.resourceDetails;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
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
                                fieldWithPath("meetingType").type(JsonFieldType.STRING).description("모임방식 [online, offline, any]"),
                                fieldWithPath("term").type(JsonFieldType.NUMBER).description("프로젝트 진행 기간"),
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
        when(projectReadService.readProject(anyLong(), anyLong()))
                .thenReturn(response);

        // expected
        mockMvc.perform(get("/projects/{projectId}", projectId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer {AccessToken}"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.projectId").value(response.getProjectId()),
                        jsonPath("$.representImageId").value(response.getRepresentImageId()),
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
                        jsonPath("$.term").value(response.getTerm()),
                        jsonPath("$.myLike").value(response.isMyLike()),
                        jsonPath("$.projectJobParticipants[0].job").value(response.getProjectJobParticipants().get(0).getJob().getValue()),
                        jsonPath("$.projectJobParticipants[0].recruitAmount").value(response.getProjectJobParticipants().get(0).getRecruitAmount()),
                        jsonPath("$.projectJobParticipants[0].participantAmount").value(response.getProjectJobParticipants().get(0).getParticipantAmount()),
                        jsonPath("$.projectJobParticipants[0].participants[0].participantId")
                                .value(response.getProjectJobParticipants().get(0).getParticipants().get(0).getParticipantId()),
                        jsonPath("$.projectJobParticipants[0].participants[0].memberId")
                                .value(response.getProjectJobParticipants().get(0).getParticipants().get(0).getMemberId()),
                        jsonPath("$.projectJobParticipants[0].participants[0].job")
                                .value(response.getProjectJobParticipants().get(0).getParticipants().get(0).getJob().getValue()),
                        jsonPath("$.projectJobParticipants[0].participants[0].nickname")
                                .value(response.getProjectJobParticipants().get(0).getParticipants().get(0).getNickname()),
                        jsonPath("$.projectJobParticipants[0].participants[0].career")
                                .value(response.getProjectJobParticipants().get(0).getParticipants().get(0).getCareer().getValue()),
                        jsonPath("$.projectJobParticipants[0].participants[1].participantId")
                                .value(response.getProjectJobParticipants().get(0).getParticipants().get(1).getParticipantId()),
                        jsonPath("$.projectJobParticipants[0].participants[1].memberId")
                                .value(response.getProjectJobParticipants().get(0).getParticipants().get(1).getMemberId()),
                        jsonPath("$.projectJobParticipants[0].participants[1].job")
                                .value(response.getProjectJobParticipants().get(0).getParticipants().get(1).getJob().getValue()),
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
                                fieldWithPath("representImageId").type(JsonFieldType.NUMBER).description("대표이미지 ID"),
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
                                fieldWithPath("term").type(JsonFieldType.NUMBER).description("프로젝트 진행 기간"),
                                fieldWithPath("myLike").type(JsonFieldType.BOOLEAN).description("좋아요 여부"),
                                fieldWithPath("projectJobParticipants").type(JsonFieldType.ARRAY).description("직무별 참여자 (팀원 구성)"),
                                fieldWithPath("projectJobParticipants[].job").type(JsonFieldType.STRING).description("직무"),
                                fieldWithPath("projectJobParticipants[].recruitAmount").type(JsonFieldType.NUMBER).description("모집 인원수"),
                                fieldWithPath("projectJobParticipants[].participantAmount").type(JsonFieldType.NUMBER).description("참여자 인원수"),
                                fieldWithPath("projectJobParticipants[].participants").type(JsonFieldType.ARRAY).description("참여자"),
                                fieldWithPath("projectJobParticipants[].participants[].participantId").type(JsonFieldType.NUMBER).description("참여 ID = 테이블 PK"),
                                fieldWithPath("projectJobParticipants[].participants[].memberId").type(JsonFieldType.NUMBER).description("참여자 ID = 멤버 ID"),
                                fieldWithPath("projectJobParticipants[].participants[].job").type(JsonFieldType.STRING).description("참여자 직무"),
                                fieldWithPath("projectJobParticipants[].participants[].nickname").type(JsonFieldType.STRING).description("닉네임"),
                                fieldWithPath("projectJobParticipants[].participants[].career").type(JsonFieldType.STRING).description("경력")
                        )
                ));
    }

    @Test
    @WithMockWhatplMember
    @DisplayName("프로젝트 리스트 검색 API Docs")
    void search() throws Exception {
        // given
        int page = 1;
        int size = 2;
        String sort = "popular";
        ProjectInfo projectInfo = ProjectInfo.builder()
                .projectId(1L)
                .title("테스트 프로젝트 1")
                .status(ProjectStatus.RECRUITING)
                .subject(Subject.SOCIAL_MEDIA)
                .skills(List.of(Skill.JAVA, Skill.FIGMA))
                .remainedJobs(List.of(RemainedJobDto.builder()
                        .job(Job.BACKEND_DEVELOPER)
                        .recruitAmount(5)
                        .remainedAmount(2)
                        .build()))
                .profitable(false)
                .views(100)
                .likes(20)
                .comments(5)
                .representImageUri("/images/default?type=project")
                .myLike(true)
                .build();
        List<ProjectInfo> projectInfos = List.of(projectInfo);
        ProjectSearchCondition searchCondition = ProjectSearchCondition.builder()
                .subject(Subject.SOCIAL_MEDIA)
                .status(ProjectStatus.RECRUITING)
                .skill(Skill.JAVA)
                .job(Job.BACKEND_DEVELOPER)
                .profitable(false)
                .keyword("테스트")
                .build();
        when(projectReadService.searchProjectList(any(Pageable.class), any(ProjectSearchCondition.class))).thenReturn(projectInfos);

        // expected
        mockMvc.perform(get("/projects")
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size))
                        .param("sort", sort)
                        .param("subject", searchCondition.getSubject().getValue())
                        .param("status", searchCondition.getStatus().getValue())
                        .param("skill", searchCondition.getSkill().getValue())
                        .param("job", searchCondition.getJob().getValue())
                        .param("profitable", searchCondition.getProfitable().toString())
                        .param("keyword", searchCondition.getKeyword())
                )
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON_VALUE),
                        jsonPath("$.list[*].projectId").value(Long.valueOf(projectInfo.getProjectId()).intValue()),
                        jsonPath("$.list[*].title").value(projectInfo.getTitle()),
                        jsonPath("$.list[*].status").value(projectInfo.getStatus().getValue()),
                        jsonPath("$.list[*].skills").exists(),
                        jsonPath("$.list[*].remainedJobs").exists(),
                        jsonPath("$.list[*].profitable").value(projectInfo.isProfitable()),
                        jsonPath("$.list[*].views").value(projectInfo.getViews()),
                        jsonPath("$.list[*].likes").value(projectInfo.getLikes()),
                        jsonPath("$.list[*].comments").value(projectInfo.getComments()),
                        jsonPath("$.list[*].representImageUri").value(projectInfo.getRepresentImageUri()),
                        jsonPath("$.list[*].myLike").value(projectInfo.isMyLike())
                )
                .andDo(print())
                .andDo(document("search-project-list",
                        resourceDetails().tag(ApiDocTag.PROJECT.getTag())
                                .summary("프로젝트 리스트 검색")
                                .description("""
                                        프로젝트 리스트를 검색합니다.
                                                                                
                                        [유효성 검증]
                                        - 요청 필드의 상태(status)값은 "모집중"만 지원합니다. (아닐 시 400 에러) (status 필드가 없으면 전체 상태 검색)
                                                                                
                                        [정렬]
                                        - 정렬은 queryParameter의 sort값을 지정해야 합니다. 아래 2가지 정렬을 지원합니다.
                                        - popular : 인기순
                                        - latest : 최신순
                                        """),
                        queryParameters(
                                parameterWithName("page").description("페이지 번호"),
                                parameterWithName("size").description("페이지 사이즈"),
                                parameterWithName("sort").description("정렬 기준"),
                                parameterWithName("subject").description("프로젝트 도메인"),
                                parameterWithName("job").description("직무"),
                                parameterWithName("skill").description("기술 스택"),
                                parameterWithName("status").description("프로젝트 상태"),
                                parameterWithName("profitable").description("수익화 여부"),
                                parameterWithName("keyword").description("검색어")
                        ),
                        responseFields(
                                fieldWithPath("list").type(JsonFieldType.ARRAY).description("프로젝트 리스트"),
                                fieldWithPath("list[].projectId").type(JsonFieldType.NUMBER).description("프로젝트 ID"),
                                fieldWithPath("list[].title").type(JsonFieldType.STRING).description("프로젝트 제목"),
                                fieldWithPath("list[].status").type(JsonFieldType.STRING).description("프로젝트 상태"),
                                fieldWithPath("list[].subject").type(JsonFieldType.STRING).description("프로젝트 도메인"),
                                fieldWithPath("list[].skills").type(JsonFieldType.ARRAY).description("프로젝트 기술 스택"),
                                fieldWithPath("list[].remainedJobs").type(JsonFieldType.ARRAY).description("남은 모집 직무(아직 모집 중인 직무)"),
                                fieldWithPath("list[].remainedJobs[].job").type(JsonFieldType.STRING).description("직무"),
                                fieldWithPath("list[].remainedJobs[].recruitAmount").type(JsonFieldType.NUMBER).description("총 모집 인원"),
                                fieldWithPath("list[].remainedJobs[].remainedAmount").type(JsonFieldType.NUMBER).description("남은 모집 인원"),
                                fieldWithPath("list[].profitable").type(JsonFieldType.BOOLEAN).description("수익화 여부"),
                                fieldWithPath("list[].views").type(JsonFieldType.NUMBER).description("조회수"),
                                fieldWithPath("list[].likes").type(JsonFieldType.NUMBER).description("좋아요 갯수"),
                                fieldWithPath("list[].comments").type(JsonFieldType.NUMBER).description("댓글 갯수"),
                                fieldWithPath("list[].representImageUri").type(JsonFieldType.STRING).description("대표 이미지 URI"),
                                fieldWithPath("list[].myLike").type(JsonFieldType.BOOLEAN).description("좋아요 여부"),
                                fieldWithPath("currentPage").type(JsonFieldType.NUMBER).description("현재 페이지"),
                                fieldWithPath("pageSize").type(JsonFieldType.NUMBER).description("페이지 사이즈"),
                                fieldWithPath("first").type(JsonFieldType.BOOLEAN).description("첫 페이지 여부"),
                                fieldWithPath("last").type(JsonFieldType.BOOLEAN).description("마지막 페이지 여부"),
                                fieldWithPath("empty").type(JsonFieldType.BOOLEAN).description("빈 리스트 여부")
                        )
                ));
    }

    @Test
    @WithMockWhatplMember
    @DisplayName("프로젝트 수정 API Docs")
    void modify() throws Exception {
        // given
        ProjectUpdateRequest projectUpdateRequest = ProjectUpdateRequestFixture.create();
        String requestJson = objectMapper.writeValueAsString(projectUpdateRequest);
        Long projectId = 1L;
        doNothing().when(projectWriteService)
                .modifyProject(anyLong(), any(ProjectUpdateRequest.class));

        // expected
        mockMvc.perform(put("/projects/{projectId}", projectId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer {AccessToken}")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(requestJson))
                .andExpectAll(
                        status().isNoContent()
                )
                .andDo(print())
                .andDo(document("update-project",
                        resourceDetails().tag(ApiDocTag.PROJECT.getTag())
                                .summary("프로젝트 수정")
                                .description("""
                                        프로젝트를 수정합니다.
                                        
                                        [유효성 검증]
                                        - 참여자가 존재하는 모집직군은 삭제할 수 없습니다. (code: PRJ17)
                                        - 모집인원은 프로젝트 참여자 수보다 적을 수 없습니다. (code: PRJ18)
                                        
                                        [권한]
                                        - 프로젝트 등록자
                                        """),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("AccessToken")
                        ),
                        pathParameters(
                                parameterWithName("projectId").description("프로젝트 ID")
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
                                fieldWithPath("meetingType").type(JsonFieldType.STRING).description("모임방식 [online, offline, any]"),
                                fieldWithPath("term").type(JsonFieldType.NUMBER).description("프로젝트 진행 기간"),
                                fieldWithPath("representImageId").type(JsonFieldType.NUMBER).description("대표이미지 ID").optional()
                        )
                ));
    }

    @Test
    @WithMockWhatplMember
    @DisplayName("프로젝트 삭제 API Docs")
    void deleteProject() throws Exception {
        // given
        long projectId = 1L;
        doNothing().when(projectWriteService)
                .deleteProject(anyLong());

        // expected
        mockMvc.perform(delete("/projects/{projectId}", projectId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer {AccessToken}"))
                .andExpectAll(
                        status().isNoContent()
                )
                .andDo(print())
                .andDo(document("delete-project",
                        resourceDetails().tag(ApiDocTag.PROJECT.getTag())
                                .summary("프로젝트 삭제")
                                .description("""
                                        프로젝트를 삭제합니다.
                                        
                                        [권한]
                                        - 프로젝트 등록자
                                        """),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("AccessToken")
                        ),
                        pathParameters(
                                parameterWithName("projectId").description("프로젝트 ID")
                        )
                ));
    }
}