package com.whatpl.chat.controller;

import com.whatpl.ApiDocTag;
import com.whatpl.BaseSecurityWebMvcTest;
import com.whatpl.chat.dto.ChatRoomResponse;
import com.whatpl.global.security.model.WithMockWhatplMember;
import com.whatpl.project.domain.enums.ApplyType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.SliceImpl;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
class ChatRoomControllerTest extends BaseSecurityWebMvcTest {

    @Test
    @WithMockWhatplMember
    @DisplayName("채팅방 리스트 API Docs")
    void readList() throws Exception {
        // given
        int page = 1;
        int size = 10;
        ChatRoomResponse chatRoomResponse = ChatRoomResponse.builder()
                .chatRoomId(1L)
                .applyType(ApplyType.APPLY)
                .projectId(1L)
                .projectTitle("프로젝트 제목")
                .opponentId(1L)
                .opponentNickname("상대방 닉네임")
                .opponentProfileImgUri("상대방 프로필 URI")
                .lastMessageContent("마지막 메시지")
                .lastMessageTime(LocalDateTime.now(Clock.fixed(
                        Instant.parse("2024-07-17T23:55:01.00Z"),
                        ZoneId.of("Asia/Seoul"))))
                .lastMessageRead(true)
                .build();
        SliceImpl<ChatRoomResponse> chatRoomResponses = new SliceImpl<>(List.of(chatRoomResponse));
        when(chatRoomService.readChatRooms(any(Pageable.class), anyLong())).thenReturn(chatRoomResponses);

        // expected
        mockMvc.perform(get("/chat/rooms")
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size))
                )
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON_VALUE),
                        jsonPath("$.list[*].chatRoomId").value(Long.valueOf(chatRoomResponse.getChatRoomId()).intValue()),
                        jsonPath("$.list[*].applyType").value(chatRoomResponse.getApplyType().name()),
                        jsonPath("$.list[*].projectId").value(Long.valueOf(chatRoomResponse.getProjectId()).intValue()),
                        jsonPath("$.list[*].projectTitle").value(chatRoomResponse.getProjectTitle()),
                        jsonPath("$.list[*].opponentId").value(Long.valueOf(chatRoomResponse.getOpponentId()).intValue()),
                        jsonPath("$.list[*].opponentNickname").value(chatRoomResponse.getOpponentNickname()),
                        jsonPath("$.list[*].opponentProfileImgUri").value(chatRoomResponse.getOpponentProfileImgUri()),
                        jsonPath("$.list[*].lastMessageContent").value(chatRoomResponse.getLastMessageContent()),
                        jsonPath("$.list[*].lastMessageTime").value(chatRoomResponse.getLastMessageTime().toString()),
                        jsonPath("$.list[*].lastMessageRead").value(chatRoomResponse.isLastMessageRead())
                )
                .andDo(print())
                .andDo(document("read-chat-room-list",
                        resourceDetails().tag(ApiDocTag.CHAT_ROOM.getTag())
                                .summary("채팅방 리스트")
                                .description("""
                                        프로젝트 리스트를 조회합니다.
                                                                                
                                        [ApplyType]
                                        - APPLY: 지원자가 프로젝트에 지원한 채팅방
                                        - OFFER: 모집자가 프로젝트에 합류 제안한 채팅방
                                        """),
                        queryParameters(
                                parameterWithName("page").description("페이지 번호"),
                                parameterWithName("size").description("페이지 사이즈")
                        ),
                        responseFields(
                                fieldWithPath("list").type(JsonFieldType.ARRAY).description("채팅방 리스트"),
                                fieldWithPath("list[].chatRoomId").type(JsonFieldType.NUMBER).description("채팅방 ID"),
                                fieldWithPath("list[].applyType").type(JsonFieldType.STRING).description("채팅방 지원 타입"),
                                fieldWithPath("list[].projectId").type(JsonFieldType.NUMBER).description("프로젝트 ID"),
                                fieldWithPath("list[].projectTitle").type(JsonFieldType.STRING).description("프로젝트 제목"),
                                fieldWithPath("list[].opponentId").type(JsonFieldType.NUMBER).description("상대방 멤버 ID"),
                                fieldWithPath("list[].opponentNickname").type(JsonFieldType.STRING).description("상대방 멤버 닉네임"),
                                fieldWithPath("list[].opponentProfileImgUri").type(JsonFieldType.STRING).description("상대방 멤버 프로필 이미지 URI"),
                                fieldWithPath("list[].lastMessageContent").type(JsonFieldType.STRING).description("마지막 메시지 내용"),
                                fieldWithPath("list[].lastMessageTime").type(JsonFieldType.STRING).description("마지막 메시지 발송 일시"),
                                fieldWithPath("list[].lastMessageRead").type(JsonFieldType.BOOLEAN).description("마지막 메시지 읽음 여부"),
                                fieldWithPath("currentPage").type(JsonFieldType.NUMBER).description("현재 페이지"),
                                fieldWithPath("pageSize").type(JsonFieldType.NUMBER).description("페이지 사이즈"),
                                fieldWithPath("first").type(JsonFieldType.BOOLEAN).description("첫 페이지 여부"),
                                fieldWithPath("last").type(JsonFieldType.BOOLEAN).description("마지막 페이지 여부"),
                                fieldWithPath("empty").type(JsonFieldType.BOOLEAN).description("빈 리스트 여부")
                        )
                ));
    }
}