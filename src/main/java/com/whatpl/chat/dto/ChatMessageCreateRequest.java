package com.whatpl.chat.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageCreateRequest {

    @NotBlank(message = "메시지 내용은 필수 입력 항목입니다.")
    private String content;
}
