package com.whatpl.chat.controller;

import com.whatpl.chat.dto.ChatRoomResponse;
import com.whatpl.chat.service.ChatRoomService;
import com.whatpl.global.pagination.SliceResponse;
import com.whatpl.global.security.domain.MemberPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    @GetMapping("/chat/rooms")
    public ResponseEntity<SliceResponse<ChatRoomResponse>> readList(Pageable pageable,
                                                                    @AuthenticationPrincipal MemberPrincipal principal) {
        Slice<ChatRoomResponse> chatRoomSlice = chatRoomService.readChatRooms(pageable, principal.getId());
        return ResponseEntity.ok(new SliceResponse<>(chatRoomSlice));
    }
}
