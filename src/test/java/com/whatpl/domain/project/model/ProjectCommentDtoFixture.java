package com.whatpl.domain.project.model;

import com.whatpl.domain.project.dto.ProjectCommentDto;
import com.whatpl.domain.project.dto.ProjectSubCommentDto;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

public class ProjectCommentDtoFixture {

    public static ProjectCommentDto create(long commentId, long writerId) {
        // 하위 댓글
        ProjectSubCommentDto subComment1 = ProjectSubCommentDto.builder()
                .commentId(commentId + 100L)
                .writerId(writerId)
                .writerNickname("테스트 멤버")
                .content("테스트 하위 댓글 내용 1")
                .createdAt(LocalDateTime.now(Clock.fixed(
                        Instant.parse("2024-04-26T19:13:43.00Z"),
                        ZoneId.of("Asia/Seoul"))))
                .isModified(false)
                .isDeleted(false)
                .parentId(commentId)
                .build();

        ProjectSubCommentDto subComment2 = ProjectSubCommentDto.builder()
                .commentId(commentId + 200L)
                .writerId(writerId)
                .writerNickname("테스트 멤버")
                .content("테스트 하위 댓글 내용 2 - 삭제된 댓글로 표기")
                .createdAt(LocalDateTime.now(Clock.fixed(
                        Instant.parse("2024-04-26T19:15:43.00Z"),
                        ZoneId.of("Asia/Seoul"))))
                .isModified(false)
                .isDeleted(true)
                .parentId(commentId)
                .build();

        return ProjectCommentDto.builder()
                .commentId(commentId)
                .writerId(writerId)
                .writerNickname("테스트 멤버")
                .content("테스트 댓글 내용")
                .createdAt(LocalDateTime.now(Clock.fixed(
                        Instant.parse("2024-04-26T19:13:43.00Z"),
                        ZoneId.of("Asia/Seoul"))))
                .isModified(false)
                .isDeleted(false)
                .subComments(List.of(subComment1, subComment2))
                .build();
    }
}
