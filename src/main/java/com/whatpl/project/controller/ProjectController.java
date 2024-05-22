package com.whatpl.project.controller;

import com.whatpl.global.exception.BizException;
import com.whatpl.global.exception.ErrorCode;
import com.whatpl.global.pagination.SliceResponse;
import com.whatpl.global.security.domain.MemberPrincipal;
import com.whatpl.project.domain.enums.ProjectStatus;
import com.whatpl.project.dto.ProjectCreateRequest;
import com.whatpl.project.dto.ProjectInfo;
import com.whatpl.project.dto.ProjectReadResponse;
import com.whatpl.project.dto.ProjectSearchCondition;
import com.whatpl.project.service.ProjectReadService;
import com.whatpl.project.service.ProjectWriteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectWriteService projectWriteService;
    private final ProjectReadService projectReadService;

    @PostMapping("/projects")
    public ResponseEntity<Void> write(@Valid @RequestBody ProjectCreateRequest request,
                                      @AuthenticationPrincipal MemberPrincipal principal) {

        Long projectId = projectWriteService.createProject(request, principal.getId());
        String createdResourceUri = String.format("/projects/%d", projectId);

        return ResponseEntity.created(URI.create(createdResourceUri)).build();
    }

    @GetMapping("/projects/{projectId}")
    public ResponseEntity<ProjectReadResponse> read(@PathVariable Long projectId,
                                                    @AuthenticationPrincipal MemberPrincipal principal) {
        long memberId = principal != null ? principal.getId() : Long.MIN_VALUE;
        ProjectReadResponse projectReadResponse = projectReadService.readProject(projectId, memberId);
        return ResponseEntity.ok(projectReadResponse);
    }

    @PostMapping("/projects/search")
    public ResponseEntity<SliceResponse<ProjectInfo>> search(Pageable pageable,
                                                             @RequestBody ProjectSearchCondition searchCondition,
                                                             @AuthenticationPrincipal MemberPrincipal principal) {
        // 프로젝트 검색조건의 프로젝트 상태는 모집중만 가능
        if (searchCondition.getStatus() != null && !ProjectStatus.RECRUITING.equals(searchCondition.getStatus())) {
            throw new BizException(ErrorCode.PROJECT_STATUS_NOT_VALID);
        }
        searchCondition.setLonginMemberId(principal != null ? principal.getId() : Long.MIN_VALUE);
        Slice<ProjectInfo> projects = projectReadService.searchProjectList(pageable, searchCondition);
        return ResponseEntity.ok(new SliceResponse<>(projects));
    }
}
