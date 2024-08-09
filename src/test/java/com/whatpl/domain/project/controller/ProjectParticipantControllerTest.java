package com.whatpl.domain.project.controller;

import com.whatpl.ApiDocTag;
import com.whatpl.BaseSecurityWebMvcTest;
import com.whatpl.global.security.model.WithMockWhatplMember;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.http.HttpHeaders;
import org.springframework.restdocs.RestDocumentationExtension;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.resourceDetails;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
class ProjectParticipantControllerTest extends BaseSecurityWebMvcTest {

    @Test
    @WithMockWhatplMember
    @DisplayName("프로젝트 참여자 제외 API Docs")
    void excludeParticipant() throws Exception {
        // given
        doNothing().when(projectParticipantService).deleteParticipant(anyLong(), anyLong());
        long projectId = 1L;
        long participantId = 1L;

        // expected
        mockMvc.perform(delete("/projects/{projectId}/participants/{participantId}", projectId, participantId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer {AccessToken}"))
                .andExpectAll(
                        status().isNoContent()
                )
                .andDo(print())
                .andDo(document("exclude-participant",
                        resourceDetails().tag(ApiDocTag.PROJECT_PARTICIPANT.getTag())
                                .summary("프로젝트 참여자 제외")
                                .description("""
                                        프로젝트 참여자를 제외합니다.
                                                                                
                                        참여자를 삭제하고, 지원서의 상태를 ACCEPTED -> EXCLUDED로 변경합니다.
                                                                                
                                        [권한]
                                        - 프로젝트 모집자
                                        """),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("AccessToken")
                        ),
                        pathParameters(
                                parameterWithName("projectId").description("프로젝트 ID"),
                                parameterWithName("participantId").description("참여 ID")
                        )
                ));
    }
}