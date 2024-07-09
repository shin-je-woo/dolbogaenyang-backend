package com.whatpl.project.service;

import com.whatpl.global.exception.BizException;
import com.whatpl.global.exception.ErrorCode;
import com.whatpl.global.util.PaginationUtils;
import com.whatpl.project.converter.ProjectModelConverter;
import com.whatpl.project.domain.Project;
import com.whatpl.project.dto.ProjectInfo;
import com.whatpl.project.dto.ProjectReadResponse;
import com.whatpl.project.dto.ProjectSearchCondition;
import com.whatpl.project.repository.ProjectLikeRepository;
import com.whatpl.project.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class ProjectReadService {

    private final ProjectRepository projectRepository;
    private final ProjectReadAsyncService projectReadAsyncService;
    private final ProjectLikeRepository projectLikeRepository;

    @Transactional
    public ProjectReadResponse readProject(final long projectId, final long memberId) {
        Project project = projectRepository.findProjectWithParticipantsById(projectId)
                .orElseThrow(() -> new BizException(ErrorCode.NOT_FOUND_PROJECT));
        boolean myLike = projectLikeRepository.existsByProjectIdAndMemberId(projectId, memberId);

        long likes = projectLikeRepository.countByProject(project);
        // 조회수 증가
        project.increaseViews();
        return ProjectModelConverter.toProjectReadResponse(project, likes, myLike);
    }

    @Transactional(readOnly = true)
    public Slice<ProjectInfo> searchProjectList(Pageable pageable, ProjectSearchCondition searchCondition) {
        List<ProjectInfo> projectInfos = projectRepository.findProjectInfos(pageable, searchCondition);
        CompletableFuture.allOf(
                projectReadAsyncService.mergeSkills(projectInfos),
                projectReadAsyncService.mergeRemainedJobs(projectInfos),
                projectReadAsyncService.mergeLikes(projectInfos),
                projectReadAsyncService.mergeComments(projectInfos)
        ).join();

        return new SliceImpl<>(projectInfos, pageable, PaginationUtils.hasNext(projectInfos, pageable.getPageSize()));
    }
}
