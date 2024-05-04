package com.whatpl.chat.controller;

import com.whatpl.chat.dto.ChatMessageCreateRequest;
import com.whatpl.chat.service.ChatService;
import com.whatpl.global.security.domain.MemberPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @PreAuthorize("hasPermission(#chatRoomId, 'CHAT_MESSAGE', 'CREATE')")
    @PostMapping("/chat/rooms/{chatRoomId}/messages")
    public ResponseEntity<Void> writeMessage(@PathVariable long chatRoomId,
                                             @Valid @RequestBody ChatMessageCreateRequest request,
                                             @AuthenticationPrincipal MemberPrincipal principal) {
        chatService.sendMessage(chatRoomId, principal.getId(), request.getContent());
        return ResponseEntity.noContent().build();
    }
}
