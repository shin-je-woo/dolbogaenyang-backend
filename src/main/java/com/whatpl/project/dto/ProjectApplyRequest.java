package com.whatpl.project.dto;

import com.whatpl.global.common.domain.enums.Job;
import com.whatpl.project.domain.enums.ApplyType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ProjectApplyRequest {

    @NotNull(message = "지원 직무는 필수 입력 항목입니다.")
    private Job applyJob;

    @NotBlank(message = "지원 글은 필수 입력 항목입니다.")
    private String content;

    @NotNull(message = "지원 타입은 필수 입력 항목입니다.")
    private ApplyType applyType;
}
