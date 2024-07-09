package com.whatpl.project.controller;

import com.whatpl.global.exception.BizException;
import com.whatpl.global.exception.ErrorCode;
import com.whatpl.global.security.domain.MemberPrincipal;
import com.whatpl.global.common.domain.enums.ApplyStatus;
import com.whatpl.project.domain.enums.ApplyType;
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

import java.util.Objects;

@RestController
@RequiredArgsConstructor
public class ProjectApplyController {

    private final ProjectApplyService projectApplyService;

    @PreAuthorize("hasPermission(#projectId, 'APPLY', #request.applyType.name())")
    @PostMapping("/projects/{projectId}/apply")
    public ResponseEntity<ApplyResponse> apply(@PathVariable Long projectId,
                                               @Valid @RequestBody ProjectApplyRequest request,
                                               @AuthenticationPrincipal MemberPrincipal principal) {
        if(ApplyType.APPLY == request.getApplyType()) {
            return ResponseEntity.ok(projectApplyService.apply(request, projectId, principal.getId()));
        } else if (ApplyType.OFFER == request.getApplyType()){
            return ResponseEntity.ok(projectApplyService.apply(request, projectId, Objects.requireNonNullElse(request.getApplicantId(), Long.MIN_VALUE)));
        } else {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PreAuthorize("hasPermission(#applyId, 'APPLY', 'STATUS')")
    @PatchMapping("/projects/{projectId}/apply/{applyId}/status")
    public ResponseEntity<Void> applyStatus(@PathVariable Long projectId,
                                            @PathVariable Long applyId,
                                            @Valid @RequestBody ProjectApplyStatusRequest request) {

        if (ApplyStatus.WAITING.equals(request.getApplyStatus())) {
            throw new BizException(ErrorCode.CANT_PROCESS_WAITING);
        }
        if (ApplyStatus.EXCLUDED.equals(request.getApplyStatus())) {
            throw new BizException(ErrorCode.CANT_PROCESS_EXCLUDED);
        }
        projectApplyService.status(projectId, applyId, request.getApplyStatus());

        return ResponseEntity.noContent().build();
    }
}
