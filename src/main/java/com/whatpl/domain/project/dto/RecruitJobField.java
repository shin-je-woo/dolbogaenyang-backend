package com.whatpl.domain.project.dto;

import com.whatpl.global.common.domain.enums.Job;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@AllArgsConstructor
@EqualsAndHashCode(of = "job")
public class RecruitJobField {
    @NotNull(message = "직무는 필수 입력 항목입니다.")
    private Job job;
    @NotNull(message = "모집인원은 필수 입력 항목입니다.")
    @Min(value = 1, message = "모집인원은 최소 1명 이상 입력 가능합니다.")
    @Max(value = 5, message = "모집인원은 최대 5명 이하 입력 가능합니다.")
    private Integer recruitAmount;
}