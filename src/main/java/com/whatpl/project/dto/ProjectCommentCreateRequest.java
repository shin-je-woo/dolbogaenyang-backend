package com.whatpl.project.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ProjectCommentCreateRequest {

    @Size(max = 300, message = "댓글은 최대 300자까지 입력 가능합니다.")
    private String content;

    private Long parentId;
}
