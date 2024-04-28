package com.whatpl.project.controller;

import com.whatpl.global.exception.BizException;
import com.whatpl.global.exception.ErrorCode;
import com.whatpl.global.security.domain.MemberPrincipal;
import com.whatpl.project.domain.enums.ApplyStatus;
import com.whatpl.project.dto.*;
import com.whatpl.project.service.ProjectApplyService;
import com.whatpl.project.service.ProjectReadService;
import com.whatpl.project.service.ProjectWriteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectWriteService projectWriteService;
    private final ProjectApplyService projectApplyService;
    private final ProjectReadService projectReadService;

    @PostMapping("/projects")
    public ResponseEntity<Void> write(@Valid @RequestBody ProjectCreateRequest request,
                                      @AuthenticationPrincipal MemberPrincipal principal) {

        Long projectId = projectWriteService.createProject(request, principal.getId());
        String createdResourceUri = String.format("/projects/%d", projectId);

        return ResponseEntity.created(URI.create(createdResourceUri)).build();
    }

    @PostMapping("/projects/{projectId}/applications")
    public ResponseEntity<Void> apply(@PathVariable Long projectId,
                                      @Valid @RequestBody ProjectApplyRequest request,
                                      @AuthenticationPrincipal MemberPrincipal principal) {

        Long applyId = projectApplyService.apply(request, projectId, principal.getId());
        String createdResourceUri = String.format("/projects/%d/applications/%d", projectId, applyId);

        return ResponseEntity.created(URI.create(createdResourceUri)).build();
    }

    @PreAuthorize("hasPermission(#applyId, 'APPLY', 'READ')")
    @GetMapping("/projects/{projectId}/applications/{applyId}")
    public ResponseEntity<ProjectApplyReadResponse> applyRead(@PathVariable Long projectId,
                                                              @PathVariable Long applyId) {

        ProjectApplyReadResponse applyReadResponse = projectApplyService.read(projectId, applyId);

        return ResponseEntity.ok(applyReadResponse);
    }

    @PreAuthorize("hasPermission(#applyId, 'APPLY', 'STATUS')")
    @PatchMapping("/projects/{projectId}/applications/{applyId}/status")
    public ResponseEntity<ProjectApplyReadResponse> applyStatus(@PathVariable Long projectId,
                                                                @PathVariable Long applyId,
                                                                @Valid @RequestBody ProjectApplyStatusRequest request) {

        if (ApplyStatus.WAITING.equals(request.getApplyStatus())) {
            throw new BizException(ErrorCode.CANT_PROCESS_WAITING);
        }
        projectApplyService.status(projectId, applyId, request.getApplyStatus());

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/projects/{projectId}")
    public ResponseEntity<ProjectReadResponse> read(@PathVariable Long projectId) {
        ProjectReadResponse projectReadResponse = projectReadService.readProject(projectId);
        return ResponseEntity.ok(projectReadResponse);
    }
}
