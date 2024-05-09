package com.whatpl.project.service;

import com.whatpl.global.exception.BizException;
import com.whatpl.global.exception.ErrorCode;
import com.whatpl.project.converter.ProjectModelConverter;
import com.whatpl.project.domain.Project;
import com.whatpl.project.domain.ProjectParticipant;
import com.whatpl.project.dto.ProjectReadResponse;
import com.whatpl.project.repository.ProjectLikeRepository;
import com.whatpl.project.repository.ProjectParticipantRepository;
import com.whatpl.project.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectReadService {

    private final ProjectRepository projectRepository;
    private final ProjectParticipantRepository projectParticipantRepository;
    private final ProjectLikeRepository projectLikeRepository;

    @Transactional
    public ProjectReadResponse readProject(final long projectId) {
        Project project = projectRepository.findProjectWithAllById(projectId)
                .orElseThrow(() -> new BizException(ErrorCode.NOT_FOUND_PROJECT));
        List<ProjectParticipant> participants = projectParticipantRepository.findAllByProjectId(project.getId());

        long likes = projectLikeRepository.countByProject(project);
        // 조회수 증가
        project.increaseViews();
        return ProjectModelConverter.toProjectReadResponse(project, participants, likes);
    }
}
