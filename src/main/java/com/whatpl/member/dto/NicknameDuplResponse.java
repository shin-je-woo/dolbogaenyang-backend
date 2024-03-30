package com.whatpl.member.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class NicknameDuplResponse {

    private final boolean success;
    private final String message;

    @Builder
    public NicknameDuplResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public static NicknameDuplResponse of(boolean success, String nickname) {
        NicknameDuplResponseBuilder builder = NicknameDuplResponse.builder()
                .success(success);
        if (success) {
            builder.message(String.format("사용 가능한 닉네임입니다. %s", nickname));
        } else {
            builder.message(String.format("이미 존재하는 닉네임입니다. %s", nickname));
        }
        return builder.build();
    }
}
