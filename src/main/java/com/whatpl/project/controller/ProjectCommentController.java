package com.whatpl.project.controller;

import com.whatpl.global.security.domain.MemberPrincipal;
import com.whatpl.project.dto.ProjectCommentCreateRequest;
import com.whatpl.project.service.ProjectCommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ProjectCommentController {

    private final ProjectCommentService projectCommentService;

    @PostMapping("/projects/{projectId}/comments")
    public ResponseEntity<Void> write(@PathVariable Long projectId,
                                      @AuthenticationPrincipal MemberPrincipal principal,
                                      @Valid @RequestBody ProjectCommentCreateRequest request) {
        projectCommentService.createProjectComment(request, projectId, principal.getId());
        return ResponseEntity.noContent().build();
    }
}
