package com.whatpl.domain.project.service;

import com.whatpl.global.exception.BizException;
import com.whatpl.global.exception.ErrorCode;
import com.whatpl.domain.project.converter.ProjectModelConverter;
import com.whatpl.domain.project.domain.Project;
import com.whatpl.domain.project.dto.ProjectInfo;
import com.whatpl.domain.project.dto.ProjectReadResponse;
import com.whatpl.domain.project.dto.ProjectSearchCondition;
import com.whatpl.domain.project.repository.ProjectLikeRepository;
import com.whatpl.domain.project.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
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
        project.increaseViews();

        return ProjectModelConverter.toProjectReadResponse(project, likes, myLike);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "projectList",
            key = "T(com.whatpl.domain.project.util.ProjectCacheUtils).buildListCacheKey(#searchCondition)",
            condition = "T(com.whatpl.domain.project.util.ProjectCacheUtils).isCacheable(#pageable, #searchCondition)")
    public List<ProjectInfo> searchProjectList(Pageable pageable, ProjectSearchCondition searchCondition) {
        List<ProjectInfo> projectInfos = projectRepository.findProjectInfos(pageable, searchCondition);
        CompletableFuture.allOf(
                projectReadAsyncService.mergeSkills(projectInfos),
                projectReadAsyncService.mergeRemainedJobs(projectInfos),
                projectReadAsyncService.mergeLikes(projectInfos),
                projectReadAsyncService.mergeComments(projectInfos)
        ).join();

        return projectInfos;
    }
}
