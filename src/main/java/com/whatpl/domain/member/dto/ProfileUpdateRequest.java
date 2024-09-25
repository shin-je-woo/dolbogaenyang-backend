package com.whatpl.domain.member.dto;

import com.whatpl.global.common.model.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.Set;

@Getter
@Builder
@AllArgsConstructor
public class ProfileUpdateRequest {

    @NotEmpty(message = "닉네임은 필수 입력 항목입니다.")
    @Pattern(regexp = "^[ㄱ-ㅎ가-힣a-z0-9-_]{2,8}$", message = "닉네임은 특수문자를 제외한 2~8자리여야 합니다.")
    private String nickname;

    @NotNull(message = "직무는 필수 입력 항목입니다.")
    private Job job;

    @NotNull(message = "경력은 필수 입력 항목입니다.")
    private Career career;

    @NotNull(message = "기술스택은 필수 입력 항목입니다.")
    @Size(min = 1, max = 10, message = "기술스택은 1 ~ 10개 입력 가능합니다.")
    private Set<Skill> skills;

    private boolean profileOpen;

    @Size(max = 5, message = "관심주제는 최대 6개 입력 가능합니다.")
    private Set<Subject> subjects;

    @Size(max = 5, message = "참고링크는 최대 5개 첨부 가능합니다.")
    private Set<String> references;

    private WorkTime workTime;
}
