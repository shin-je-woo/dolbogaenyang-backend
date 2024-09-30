package com.whatpl.domain.member.controller;

import com.whatpl.ApiDocTag;
import com.whatpl.BaseSecurityWebMvcTest;
import com.whatpl.domain.project.dto.ParticipatedProject;
import com.whatpl.global.common.model.Job;
import com.whatpl.global.common.model.Subject;
import com.whatpl.global.security.model.WithMockWhatplMember;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.http.HttpHeaders;
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
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
                                fieldWithPath("participatedProjects").type(JsonFieldType.ARRAY).description("프로젝트 ID"),
                                fieldWithPath("participatedProjects[].projectId").type(JsonFieldType.NUMBER).description("프로젝트 ID"),
                                fieldWithPath("participatedProjects[].title").type(JsonFieldType.STRING).description("프로젝트 제목"),
                                fieldWithPath("participatedProjects[].representImageId").type(JsonFieldType.NUMBER).description("프로젝트 대표 이미지 ID"),
                                fieldWithPath("participatedProjects[].subject").type(JsonFieldType.STRING).description("프로젝트 주제"),
                                fieldWithPath("participatedProjects[].job").type(JsonFieldType.STRING).description("참여한 직무"),
                                fieldWithPath("participatedProjects[].participatedAt").type(JsonFieldType.STRING).description("참여 일시")
                        )
                ));
    }
}