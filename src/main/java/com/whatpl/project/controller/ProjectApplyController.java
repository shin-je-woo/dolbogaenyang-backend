package com.whatpl.project.controller;

import com.whatpl.global.exception.BizException;
import com.whatpl.global.exception.ErrorCode;
import com.whatpl.global.security.domain.MemberPrincipal;
import com.whatpl.project.domain.enums.ApplyStatus;
import com.whatpl.project.dto.ApplyResponse;
import com.whatpl.project.dto.ProjectApplyRequest;
import com.whatpl.project.dto.ProjectApplyStatusRequest;
import com.whatpl.project.service.ProjectApplyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ProjectApplyController {

    private final ProjectApplyService projectApplyService;

    @PostMapping("/projects/{projectId}/apply")
    public ResponseEntity<ApplyResponse> apply(@PathVariable Long projectId,
                                               @Valid @RequestBody ProjectApplyRequest request,
                                               @AuthenticationPrincipal MemberPrincipal principal) {

        ApplyResponse applyResponse = projectApplyService.apply(request, projectId, principal.getId());

        return ResponseEntity.ok(applyResponse);
    }

    @PreAuthorize("hasPermission(#applyId, 'APPLY', 'STATUS')")
    @PatchMapping("/projects/{projectId}/apply/{applyId}/status")
    public ResponseEntity<Void> applyStatus(@PathVariable Long projectId,
                                            @PathVariable Long applyId,
                                            @Valid @RequestBody ProjectApplyStatusRequest request) {

        if (ApplyStatus.WAITING.equals(request.getApplyStatus())) {
            throw new BizException(ErrorCode.CANT_PROCESS_WAITING);
        }
        projectApplyService.status(projectId, applyId, request.getApplyStatus());

        return ResponseEntity.noContent().build();
    }
}
