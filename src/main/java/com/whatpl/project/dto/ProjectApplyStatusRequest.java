package com.whatpl.project.dto;

import com.whatpl.project.domain.enums.ApplyStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectApplyStatusRequest {

    @NotNull(message = "지원서 상태는 필수값입니다.")
    private ApplyStatus applyStatus;
}
