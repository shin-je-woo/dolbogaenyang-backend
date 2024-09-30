package com.whatpl.domain.member.controller;

import com.whatpl.ApiDocTag;
import com.whatpl.BaseSecurityWebMvcTest;
import com.whatpl.domain.project.dto.ParticipatedProject;
import com.whatpl.domain.project.dto.ProjectInfo;
import com.whatpl.domain.project.dto.RemainedJobDto;
import com.whatpl.domain.project.model.ProjectStatus;
import com.whatpl.global.common.model.Job;
import com.whatpl.global.common.model.Skill;
import com.whatpl.global.common.model.Subject;
import com.whatpl.global.security.model.WithMockWhatplMember;
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
import java.util.List;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.resourceDetails;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
class MemberProjectControllerTest extends BaseSecurityWebMvcTest {

    @Test
    @WithMockWhatplMember
    @DisplayName("참여한 프로젝트 조회")
    void readParticipatedProject() throws Exception {
        // given
        ParticipatedProject result = ParticipatedProject.builder()
                .projectId(1L)
                .title("프로젝트1")
                .representImageId(1L)
                .subject(Subject.ART)
                .job(Job.BACKEND_DEVELOPER)
                .participatedAt(LocalDateTime.now(Clock.fixed(
                        Instant.parse("2024-09-30T23:55:01.00Z"),
                        ZoneId.of("Asia/Seoul"))))
                .build();
        when(memberProjectService.readParticipatedProjects(anyLong())).thenReturn(List.of(result));

        // expected
        mockMvc.perform(get("/members/{memberId}/projects/participated", 1L)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer {AccessToken}"))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("read-participated-project",
                        resourceDetails()
                                .tag(ApiDocTag.MEMBER.getTag())
                                .summary("참여한 프로젝트 조회")
                                .description("""
                                        멤버의 참여한 프로젝트를 조회합니다.
                                        """),
                        pathParameters(
                                parameterWithName("memberId").description("멤버 ID")
                        ),
                        responseFields(
                                fieldWithPath("participatedProjects").type(JsonFieldType.ARRAY).description("프로젝트 리스트"),
                                fieldWithPath("participatedProjects[].projectId").type(JsonFieldType.NUMBER).description("프로젝트 ID"),
                                fieldWithPath("participatedProjects[].title").type(JsonFieldType.STRING).description("프로젝트 제목"),
                                fieldWithPath("participatedProjects[].representImageId").type(JsonFieldType.NUMBER).description("프로젝트 대표 이미지 ID"),
                                fieldWithPath("participatedProjects[].subject").type(JsonFieldType.STRING).description("프로젝트 주제"),
                                fieldWithPath("participatedProjects[].job").type(JsonFieldType.STRING).description("참여한 직무"),
                                fieldWithPath("participatedProjects[].participatedAt").type(JsonFieldType.STRING).description("참여 일시")
                        )
                ));
    }

    @Test
    @WithMockWhatplMember
    @DisplayName("모집한 프로젝트 조회")
    void readRecruitedProject() throws Exception {
        // given
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
                .representImageId(1L)
                .myLike(true)
                .build();
        List<ProjectInfo> projectInfos = List.of(projectInfo);
        when(memberProjectService.readRecruitedProjects(anyLong())).thenReturn(projectInfos);

        // expected
        mockMvc.perform(get("/members/{memberId}/projects/recruited", 1L)
                .header(HttpHeaders.AUTHORIZATION, "Bearer {AccessToken}"))
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andDo(print())
                .andDo(document("read-recruited-project",
                        resourceDetails().tag(ApiDocTag.MEMBER.getTag())
                                .summary("모집한 프로젝트 조회")
                                .description("""
                                        멤버의 모집한 프로젝트를 조회합니다.
                                        """),
                        pathParameters(
                                parameterWithName("memberId").description("멤버 ID")
                        ),
                        responseFields(
                                fieldWithPath("recruitedProjects").type(JsonFieldType.ARRAY).description("프로젝트 리스트"),
                                fieldWithPath("recruitedProjects[].projectId").type(JsonFieldType.NUMBER).description("프로젝트 ID"),
                                fieldWithPath("recruitedProjects[].title").type(JsonFieldType.STRING).description("프로젝트 제목"),
                                fieldWithPath("recruitedProjects[].status").type(JsonFieldType.STRING).description("프로젝트 상태"),
                                fieldWithPath("recruitedProjects[].subject").type(JsonFieldType.STRING).description("프로젝트 도메인"),
                                fieldWithPath("recruitedProjects[].skills").type(JsonFieldType.ARRAY).description("프로젝트 기술 스택"),
                                fieldWithPath("recruitedProjects[].remainedJobs").type(JsonFieldType.ARRAY).description("남은 모집 직무(아직 모집 중인 직무)"),
                                fieldWithPath("recruitedProjects[].remainedJobs[].job").type(JsonFieldType.STRING).description("직무"),
                                fieldWithPath("recruitedProjects[].remainedJobs[].recruitAmount").type(JsonFieldType.NUMBER).description("총 모집 인원"),
                                fieldWithPath("recruitedProjects[].remainedJobs[].remainedAmount").type(JsonFieldType.NUMBER).description("남은 모집 인원"),
                                fieldWithPath("recruitedProjects[].profitable").type(JsonFieldType.BOOLEAN).description("수익화 여부"),
                                fieldWithPath("recruitedProjects[].views").type(JsonFieldType.NUMBER).description("조회수"),
                                fieldWithPath("recruitedProjects[].likes").type(JsonFieldType.NUMBER).description("좋아요 갯수"),
                                fieldWithPath("recruitedProjects[].comments").type(JsonFieldType.NUMBER).description("댓글 갯수"),
                                fieldWithPath("recruitedProjects[].representImageId").type(JsonFieldType.NUMBER).description("대표 이미지 ID"),
                                fieldWithPath("recruitedProjects[].myLike").type(JsonFieldType.BOOLEAN).description("좋아요 여부")
                        )
                ));
    }
}