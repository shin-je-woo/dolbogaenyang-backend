package com.whatpl.project.controller;

import com.whatpl.ApiDocTag;
import com.whatpl.BaseSecurityWebMvcTest;
import com.whatpl.global.security.domain.MemberPrincipal;
import com.whatpl.global.security.model.WithMockWhatplMember;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.data.util.CastUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.core.context.SecurityContextHolder;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.resourceDetails;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
class ProjectLikeControllerTest extends BaseSecurityWebMvcTest {

    @Test
    @WithMockWhatplMember
    @DisplayName("프로젝트 좋아요 등록 API Docs")
    void putLike() throws Exception {
        // given
        long projectId = 1L;
        long likeId = 1L;
        when(projectLikeService.putLike(anyLong(), anyLong()))
                .thenReturn(likeId);
        MemberPrincipal principal = CastUtils.cast(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        long memberId = principal.getId();

        // expected
        mockMvc.perform(put("/projects/{projectId}/likes", projectId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer {AccessToken}"))
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON_VALUE),
                        jsonPath("$.likeId").value(likeId),
                        jsonPath("$.projectId").value(projectId),
                        jsonPath("$.memberId").value(memberId)
                )
                .andDo(print())
                .andDo(document("put-project-like",
                        resourceDetails().tag(ApiDocTag.PROJECT_LIKE.getTag())
                                .summary("프로젝트 좋아요 등록")
                                .description("""
                                        프로젝트 좋아요를 등록합니다.
                                                                                
                                        PUT 메소드로 사용해 항상 동일한 결과를 보장합니다. (멱등성)
                                        """),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("AccessToken")
                        ),
                        responseFields(
                                fieldWithPath("likeId").type(JsonFieldType.NUMBER).description("등록된 좋아요 ID"),
                                fieldWithPath("projectId").type(JsonFieldType.NUMBER).description("프로젝트 ID"),
                                fieldWithPath("memberId").type(JsonFieldType.NUMBER).description("등록자 ID")
                        )
                ));
    }

    @Test
    @WithMockWhatplMember
    @DisplayName("프로젝트 좋아요 삭제 API Docs")
    void deleteLike() throws Exception {
        // given
        doNothing().when(projectLikeService).deleteLike(anyLong(), anyLong());

        // expected
        mockMvc.perform(delete("/projects/{projectId}/likes", 1L, 1L)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer {AccessToken}"))
                .andExpectAll(
                        status().isNoContent()
                )
                .andDo(print())
                .andDo(document("delete-project-like",
                        resourceDetails().tag(ApiDocTag.PROJECT_LIKE.getTag())
                                .summary("프로젝트 좋아요 삭제")
                                .description("""
                                        프로젝트 좋아요를 삭제합니다.
                                        
                                        [권한]
                                        
                                        - 프로젝트 좋아요 등록자
                                        """),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("AccessToken")
                        )
                ));
    }
}