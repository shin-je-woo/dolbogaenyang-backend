package com.whatpl.chat.controller;

import com.whatpl.ApiDocTag;
import com.whatpl.BaseSecurityWebMvcTest;
import com.whatpl.chat.dto.ChatMessageCreateRequest;
import com.whatpl.chat.dto.ChatMessageDto;
import com.whatpl.global.security.model.WithMockWhatplMember;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.SliceImpl;
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
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
class ChatMessageControllerTest extends BaseSecurityWebMvcTest {

    @Test
    @WithMockWhatplMember
    @DisplayName("메시지 발송 API Docs")
    void writeMessage() throws Exception {
        // given
        doNothing().when(chatMessageService).sendMessage(anyLong(), anyLong(), anyString());
        ChatMessageCreateRequest request = ChatMessageCreateRequest.builder()
                .content("메시지 내용")
                .build();
        String requestJson = objectMapper.writeValueAsString(request);

        // expected
        mockMvc.perform(post("/chat/rooms/{chatRoomId}/messages", 1L)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer {AccessToken}")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(requestJson))
                .andExpectAll(
                        status().isNoContent()
                )
                .andDo(print())
                .andDo(document("send-message",
                        resourceDetails().tag(ApiDocTag.CHAT_MESSAGE.getTag())
                                .summary("메시지 발송")
                                .description("""
                                        메시지를 발송합니다.
                                                                                
                                        [권한]
                                                                                
                                        - 프로젝트 지원자 or 모집자
                                        """),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("AccessToken")
                        ),
                        pathParameters(
                                parameterWithName("chatRoomId").description("채팅방 ID")
                        ),
                        requestFields(
                                fieldWithPath("content").type(JsonFieldType.STRING).description("메시지 내용")
                        )
                ));
    }

    @Test
    @WithMockWhatplMember
    @DisplayName("메시지 리스트 조회 API Docs")
    void readMessageList() throws Exception {
        // given
        int page = 1;
        int size = 2;
        ChatMessageDto chatMessageDto = ChatMessageDto.builder()
                .messageId(1L)
                .content("메시지입니다~")
                .senderId(1L)
                .senderNickname("왓플테스트멤버1")
                .senderProfileImgUri(null) // TODO 보낸 사람 프로필 이미지 URI
                .sendAt(LocalDateTime.now(Clock.fixed(
                        Instant.parse("2024-05-06T23:24:43.00Z"),
                        ZoneId.of("Asia/Seoul"))))
                .readAt(LocalDateTime.now(Clock.fixed(
                        Instant.parse("2024-05-07T08:13:11.00Z"),
                        ZoneId.of("Asia/Seoul"))))
                .build();
        SliceImpl<ChatMessageDto> chatMessageSlice = new SliceImpl<>(List.of(chatMessageDto));
        when(chatMessageService.readMessages(anyLong(), any(Pageable.class), anyLong()))
                .thenReturn(chatMessageSlice);

        // expected
        mockMvc.perform(get("/chat/rooms/{chatRoomId}/messages", 1L)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer {AccessToken}")
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size)))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.list").exists(),
                        jsonPath("$.list[*].messageId").value(Long.valueOf(chatMessageDto.getMessageId()).intValue()),
                        jsonPath("$.list[*].content").value(chatMessageDto.getContent()),
                        jsonPath("$.list[*].senderId").value(Long.valueOf(chatMessageDto.getSenderId()).intValue()),
                        jsonPath("$.list[*].senderNickname").value(chatMessageDto.getSenderNickname()),
                        jsonPath("$.list[*].senderProfileImgUri").value(chatMessageDto.getSenderProfileImgUri()),
                        jsonPath("$.list[*].sendAt").value(chatMessageDto.getSendAt().toString()),
                        jsonPath("$.list[*].readAt").value(chatMessageDto.getReadAt().toString()),
                        jsonPath("$.currentPage").value(page),
                        jsonPath("$.pageSize").value(chatMessageSlice.getSize()),
                        jsonPath("$.first").value(chatMessageSlice.isFirst()),
                        jsonPath("$.last").value(chatMessageSlice.isLast()),
                        jsonPath("$.empty").value(chatMessageSlice.isEmpty())
                )
                .andDo(print())
                .andDo(document("read-message-list",
                        resourceDetails().tag(ApiDocTag.CHAT_MESSAGE.getTag())
                                .summary("메시지 리스트 조회")
                                .description("""
                                        메시지 리스트를 조회합니다.
                                                                                
                                        메시지 리스트를 조회하면 발송자가 발송한 메시지를 전부 읽음 처리 합니다. (읽은 시간 update)
                                                                                
                                        [권한]
                                                                                
                                        - 프로젝트 지원자 or 모집자
                                        """),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("AccessToken")
                        ),
                        pathParameters(
                                parameterWithName("chatRoomId").description("채팅방 ID")
                        ),
                        queryParameters(
                                parameterWithName("page").description("페이지 번호"),
                                parameterWithName("size").description("페이지 사이즈")
                        ),
                        responseFields(
                                fieldWithPath("list").type(JsonFieldType.ARRAY).description("메시지 리스트"),
                                fieldWithPath("list[].messageId").type(JsonFieldType.NUMBER).description("메시지 ID"),
                                fieldWithPath("list[].content").type(JsonFieldType.STRING).description("메시지 내용"),
                                fieldWithPath("list[].senderId").type(JsonFieldType.NUMBER).description("발송자 ID"),
                                fieldWithPath("list[].senderNickname").type(JsonFieldType.STRING).description("발송자 닉네임"),
                                fieldWithPath("list[].senderProfileImgUri").type(JsonFieldType.NULL).description("발송자 프로필 이미지 URI"), // TODO 보낸 사람 프로필 이미지 URI
                                fieldWithPath("list[].sendAt").type(JsonFieldType.STRING).description("발송 일시"),
                                fieldWithPath("list[].readAt").type(JsonFieldType.STRING).description("수신 일시"),
                                fieldWithPath("currentPage").type(JsonFieldType.NUMBER).description("현재 페이지"),
                                fieldWithPath("pageSize").type(JsonFieldType.NUMBER).description("페이지 사이즈"),
                                fieldWithPath("first").type(JsonFieldType.BOOLEAN).description("첫 페이지 여부"),
                                fieldWithPath("last").type(JsonFieldType.BOOLEAN).description("마지막 페이지 여부"),
                                fieldWithPath("empty").type(JsonFieldType.BOOLEAN).description("빈 리스트 여부")
                        )
                ));
    }
}