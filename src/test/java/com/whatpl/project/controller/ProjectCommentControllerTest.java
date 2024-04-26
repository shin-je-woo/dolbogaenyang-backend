package com.whatpl.project.controller;

import com.whatpl.ApiDocTag;
import com.whatpl.BaseSecurityWebMvcTest;
import com.whatpl.global.security.model.WithMockWhatplMember;
import com.whatpl.project.dto.ProjectCommentCreateRequest;
import com.whatpl.project.dto.ProjectCommentDto;
import com.whatpl.project.dto.ProjectCommentListResponse;
import com.whatpl.project.dto.ProjectCommentUpdateRequest;
import com.whatpl.project.model.ProjectCommentDtoFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.JsonFieldType;

import java.util.List;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.resourceDetails;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
class ProjectCommentControllerTest extends BaseSecurityWebMvcTest {

    @Test
    @DisplayName("프로젝트 댓글 리스트 조회 API Docs")
    void readList() throws Exception {
        // given
        long projectId = 1L;
        long commentId = 1L;
        long writerId = 1L;
        ProjectCommentDto projectCommentDto = ProjectCommentDtoFixture.create(commentId, writerId);
        ProjectCommentListResponse response = ProjectCommentListResponse.builder()
                .projectId(projectId)
                .comments(List.of(projectCommentDto))
                .build();
        when(projectCommentService.readProjectCommentList(anyLong())).thenReturn(response);

        // expected
        mockMvc.perform(get("/projects/{projectId}/comments", projectId))
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.projectId").value(projectId),
                        jsonPath("$.comments[*].commentId").value(Long.valueOf(commentId).intValue()),
                        jsonPath("$.comments[*].writerId").value(Long.valueOf(writerId).intValue()),
                        jsonPath("$.comments[*].writerId").value(Long.valueOf(writerId).intValue()),
                        jsonPath("$.comments[*].writerNickname").value(projectCommentDto.getWriterNickname()),
                        jsonPath("$.comments[*].content").value(projectCommentDto.getContent()),
                        jsonPath("$.comments[*].createdAt").value(projectCommentDto.getCreatedAt().toString()),
                        jsonPath("$.comments[*].subComments").exists()
                )
                .andDo(print())
                .andDo(document("read-project-comment-list",
                        resourceDetails().tag(ApiDocTag.PROJECT_COMMENT.getTag())
                                .summary("프로젝트 댓글 리스트 조회")
                                .description("""
                                        프로젝트 댓글 리스트를 조회합니다.
                                        
                                        삭제된 댓글일 경우 content 값을 '삭제된 댓글입니다'로 응답합니다.
                                        """),
                        pathParameters(
                                parameterWithName("projectId").description("프로젝트 ID")
                        ),
                        responseFields(
                                fieldWithPath("projectId").type(JsonFieldType.NUMBER).description("프로젝트 ID"),
                                fieldWithPath("comments").type(JsonFieldType.ARRAY).description("댓글 리스트"),
                                fieldWithPath("comments[].commentId").type(JsonFieldType.NUMBER).description("댓글 ID"),
                                fieldWithPath("comments[].writerId").type(JsonFieldType.NUMBER).description("작성자 ID"),
                                fieldWithPath("comments[].writerNickname").type(JsonFieldType.STRING).description("작성자 닉네임"),
                                fieldWithPath("comments[].content").type(JsonFieldType.STRING).description("댓글 내용"),
                                fieldWithPath("comments[].createdAt").type(JsonFieldType.STRING).description("작성 일시"),
                                fieldWithPath("comments[].isModified").type(JsonFieldType.BOOLEAN).description("수정 여부"),
                                fieldWithPath("comments[].isDeleted").type(JsonFieldType.BOOLEAN).description("삭제 여부"),
                                fieldWithPath("comments[].subComments").type(JsonFieldType.ARRAY).description("하위 댓글 리스트"),
                                fieldWithPath("comments[].subComments[].commentId").type(JsonFieldType.NUMBER).description("댓글 ID"),
                                fieldWithPath("comments[].subComments[].writerId").type(JsonFieldType.NUMBER).description("작성자 ID"),
                                fieldWithPath("comments[].subComments[].writerNickname").type(JsonFieldType.STRING).description("작성자 닉네임"),
                                fieldWithPath("comments[].subComments[].content").type(JsonFieldType.STRING).description("댓글 내용"),
                                fieldWithPath("comments[].subComments[].createdAt").type(JsonFieldType.STRING).description("작성 일시"),
                                fieldWithPath("comments[].subComments[].isModified").type(JsonFieldType.BOOLEAN).description("수정 여부"),
                                fieldWithPath("comments[].subComments[].isDeleted").type(JsonFieldType.BOOLEAN).description("삭제 여부"),
                                fieldWithPath("comments[].subComments[].parentId").type(JsonFieldType.NUMBER).description("상위 댓글 ID")
                        )
                ));
    }

    @Test
    @WithMockWhatplMember
    @DisplayName("프로젝트 댓글 등록 API Docs")
    void write() throws Exception {
        // given
        ProjectCommentCreateRequest request = ProjectCommentCreateRequest.builder()
                .content("1번 댓글의 하위 댓글입니다.")
                .parentId(1L)
                .build();
        String requestJson = objectMapper.writeValueAsString(request);
        doNothing().when(projectCommentService).createProjectComment(any(ProjectCommentCreateRequest.class), anyLong(), anyLong());

        // expected
        mockMvc.perform(post("/projects/{projectId}/comments", 1L)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer {AccessToken}")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(requestJson))
                .andExpectAll(
                        status().isNoContent()
                )
                .andDo(print())
                .andDo(document("create-project-comment",
                        resourceDetails().tag(ApiDocTag.PROJECT_COMMENT.getTag())
                                .summary("프로젝트 댓글 등록"),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("AccessToken")
                        ),
                        pathParameters(
                                parameterWithName("projectId").description("프로젝트 ID")
                        ),
                        requestFields(
                                fieldWithPath("content").type(JsonFieldType.STRING).description("댓글 내용"),
                                fieldWithPath("parentId").type(JsonFieldType.NUMBER).description("상위 댓글 ID").optional()
                        )
                ));
    }

    @Test
    @WithMockWhatplMember
    @DisplayName("프로젝트 댓글 수정 API Docs")
    void modify() throws Exception {
        // given
        ProjectCommentUpdateRequest request = new ProjectCommentUpdateRequest("수정할 댓글 내용입니다.");
        String requestJson = objectMapper.writeValueAsString(request);
        doNothing().when(projectCommentService).updateProjectComment(any(ProjectCommentUpdateRequest.class), anyLong(), anyLong());

        // expected
        mockMvc.perform(patch("/projects/{projectId}/comments/{commentId}", 1L, 1L)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer {AccessToken}")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(requestJson))
                .andExpectAll(
                        status().isNoContent()
                )
                .andDo(print())
                .andDo(document("create-project-update",
                        resourceDetails().tag(ApiDocTag.PROJECT_COMMENT.getTag())
                                .summary("프로젝트 댓글 수정")
                                .description("""
                                        프로젝트 댓글을 수정합니다.
                                        
                                        Authorization - 프로젝트 댓글 작성자
                                        """),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("AccessToken")
                        ),
                        pathParameters(
                                parameterWithName("projectId").description("프로젝트 ID"),
                                parameterWithName("commentId").description("댓글 ID")
                        ),
                        requestFields(
                                fieldWithPath("content").type(JsonFieldType.STRING).description("댓글 내용")
                        )
                ));
    }

    @Test
    @WithMockWhatplMember
    @DisplayName("프로젝트 댓글 삭제 API Docs")
    void delete_docs() throws Exception {
        // given
        doNothing().when(projectCommentService).deleteProjectComment(anyLong(), anyLong());

        // expected
        mockMvc.perform(delete("/projects/{projectId}/comments/{commentId}", 1L, 1L)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer {AccessToken}"))
                .andExpectAll(
                        status().isNoContent()
                )
                .andDo(print())
                .andDo(document("create-project-delete",
                        resourceDetails().tag(ApiDocTag.PROJECT_COMMENT.getTag())
                                .summary("프로젝트 댓글 삭제")
                                .description("""
                                        프로젝트 댓글을 삭제합니다.
                                        
                                        Authorization - 프로젝트 댓글 작성자
                                        """),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("AccessToken")
                        ),
                        pathParameters(
                                parameterWithName("projectId").description("프로젝트 ID"),
                                parameterWithName("commentId").description("댓글 ID")
                        )
                ));
    }
}