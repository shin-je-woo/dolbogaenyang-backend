package com.whatpl.project.controller;

import com.whatpl.global.security.domain.MemberPrincipal;
import com.whatpl.project.dto.ProjectLikeResponse;
import com.whatpl.project.service.ProjectLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ProjectLikeController {

    private final ProjectLikeService projectLikeService;

    @PutMapping("/projects/{projectId}/likes")
    public ResponseEntity<ProjectLikeResponse> putLike(@PathVariable long projectId,
                                     @AuthenticationPrincipal MemberPrincipal principal) {
        long likeId = projectLikeService.putLike(projectId, principal.getId());
        return ResponseEntity.ok(ProjectLikeResponse.builder()
                .likeId(likeId)
                .projectId(projectId)
                .memberId(principal.getId())
                .build());
    }

    @PreAuthorize("hasPermission(#likeId, 'PROJECT_LIKE', 'DELETE')")
    @DeleteMapping("/projects/{projectId}/likes/{likeId}")
    public ResponseEntity<Void> deleteLike(@PathVariable long projectId,
                                           @PathVariable long likeId) {
        projectLikeService.deleteLike(projectId, likeId);
        return ResponseEntity.noContent().build();
    }
}
