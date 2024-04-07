package com.whatpl.member.dto;

import com.whatpl.global.common.domain.enums.Career;
import com.whatpl.global.common.domain.enums.Job;
import com.whatpl.global.common.domain.enums.Skill;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.Set;

@Getter
@Builder
@AllArgsConstructor
public class ProfileRequiredRequest {

    @NotEmpty(message = "닉네임은 필수 입력 항목입니다.")
    @Pattern(regexp = "^[ㄱ-ㅎ가-힣a-z0-9-_]{2,8}$", message = "닉네임은 특수문자를 제외한 2~8자리여야 합니다.")
    private String nickname;

    @NotNull(message = "직무는 필수 입력 항목입니다.")
    private Job job;

    @NotNull(message = "경력은 필수 입력 항목입니다.")
    private Career career;

    @NotNull(message = "기술스택은 필수 입력 항목입니다.")
    @NotEmpty(message = "기술스택은 필수 입력 항목입니다.")
    private Set<Skill> skills;

    private boolean profileOpen;
}
