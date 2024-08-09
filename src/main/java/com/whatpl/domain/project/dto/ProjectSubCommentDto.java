package com.whatpl.domain.project.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ProjectSubCommentDto {
    private final long commentId;
    private final long writerId;
    private final String writerNickname;
    private final String content;
    private final LocalDateTime createdAt;
    private final Boolean isModified;
    private final Boolean isDeleted;
    private final long parentId;

    @Builder
    public ProjectSubCommentDto(long commentId, long writerId, String writerNickname, String content, LocalDateTime createdAt, Boolean isModified, Boolean isDeleted, long parentId) {
        this.commentId = commentId;
        this.writerId = writerId;
        this.writerNickname = writerNickname;
        // 삭제된 댓글일 경우 내용 별도 표기
        this.content = isDeleted ? "삭제된 댓글입니다." : content;
        this.createdAt = createdAt;
        this.isModified = isModified;
        this.isDeleted = isDeleted;
        this.parentId = parentId;
    }
}