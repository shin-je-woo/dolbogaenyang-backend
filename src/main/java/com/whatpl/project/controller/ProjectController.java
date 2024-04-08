package com.whatpl.project.controller;

import com.whatpl.global.security.domain.MemberPrincipal;
import com.whatpl.project.dto.ProjectApplyRequest;
import com.whatpl.project.dto.ProjectCreateRequest;
import com.whatpl.project.service.ProjectApplyService;
import com.whatpl.project.service.ProjectWriteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectWriteService projectWriteService;
    private final ProjectApplyService projectApplyService;

    @PostMapping("/projects")
    public ResponseEntity<Void> write(@Valid @RequestBody ProjectCreateRequest request,
                                      @AuthenticationPrincipal MemberPrincipal principal) {

        Long projectId = projectWriteService.createProject(request, principal.getId());
        String createdResourceUri = String.format("/projects/%d", projectId);

        return ResponseEntity.created(URI.create(createdResourceUri)).build();
    }

    @PostMapping("/projects/{projectId}/apply")
    public ResponseEntity<Void> apply(@PathVariable Long projectId,
                                      @Valid @RequestBody ProjectApplyRequest request,
                                      @AuthenticationPrincipal MemberPrincipal principal) {

        Long applyId = projectApplyService.apply(request, projectId, principal.getId());
        String createdResourceUri = String.format("/projects/%d/apply", applyId);

        return ResponseEntity.created(URI.create(createdResourceUri)).build();
    }
}
