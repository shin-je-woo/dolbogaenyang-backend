package com.whatpl.chat.controller;

import com.whatpl.chat.dto.ChatMessageCreateRequest;
import com.whatpl.chat.dto.ChatMessageDto;
import com.whatpl.chat.service.ChatMessageService;
import com.whatpl.global.pagination.SliceResponse;
import com.whatpl.global.security.domain.MemberPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ChatMessageController {

    private final ChatMessageService chatMessageService;

    @PreAuthorize("hasPermission(#chatRoomId, 'CHAT_MESSAGE', 'CREATE')")
    @PostMapping("/chat/rooms/{chatRoomId}/messages")
    public ResponseEntity<Void> writeMessage(@PathVariable long chatRoomId,
                                             @Valid @RequestBody ChatMessageCreateRequest request,
                                             @AuthenticationPrincipal MemberPrincipal principal) {
        chatMessageService.sendMessage(chatRoomId, principal.getId(), request.getContent());
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasPermission(#chatRoomId, 'CHAT_MESSAGE', 'READ')")
    @GetMapping("/chat/rooms/{chatRoomId}/messages")
    public ResponseEntity<SliceResponse<ChatMessageDto>> readMessageList(@PathVariable long chatRoomId,
                                                                         Pageable pageable,
                                                                         @AuthenticationPrincipal MemberPrincipal principal) {
        Slice<ChatMessageDto> chatMessageSlice = chatMessageService.readMessages(chatRoomId, pageable, principal.getId());
        return ResponseEntity.ok(new SliceResponse<>(chatMessageSlice));
    }
}
