package com.whatpl.project.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.whatpl.global.exception.BizException;
import com.whatpl.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum ProjectStatus {

    RECRUITING("모집중"),
    COMPLETED("모집완료"),
    DELETED("삭제");

    @JsonValue
    private final String value;

    @JsonCreator
    public static ProjectStatus from(String value) {
        return Arrays.stream(ProjectStatus.values())
                .filter(projectStatus -> projectStatus.getValue().equals(value))
                .findFirst()
                .orElseThrow(() -> new BizException(ErrorCode.PROJECT_STATUS_NOT_VALID));
    }
}
