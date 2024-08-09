package com.whatpl.domain.project.service;

import com.whatpl.global.exception.BizException;
import com.whatpl.global.exception.ErrorCode;
import com.whatpl.domain.member.domain.Member;
import com.whatpl.domain.member.repository.MemberRepository;
import com.whatpl.domain.project.converter.ProjectCommentModelConverter;
import com.whatpl.domain.project.domain.Project;
import com.whatpl.domain.project.domain.ProjectComment;
import com.whatpl.domain.project.dto.ProjectCommentCreateRequest;
import com.whatpl.domain.project.dto.ProjectCommentDto;
import com.whatpl.domain.project.dto.ProjectCommentListResponse;
import com.whatpl.domain.project.dto.ProjectCommentUpdateRequest;
import com.whatpl.domain.project.repository.ProjectCommentRepository;
import com.whatpl.domain.project.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectCommentService {

    private final ProjectRepository projectRepository;
    private final MemberRepository memberRepository;
    private final ProjectCommentRepository projectCommentRepository;

    @Transactional
    @CacheEvict(value = "projectList", allEntries = true)
    public void createProjectComment(final ProjectCommentCreateRequest request, final long projectId, final long writerId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new BizException(ErrorCode.NOT_FOUND_PROJECT));
        Member writer = memberRepository.findById(writerId)
                .orElseThrow(() -> new BizException(ErrorCode.NOT_FOUND_MEMBER));
        // 상위 댓글
        ProjectComment parent = request.getParentId() != null ?
                projectCommentRepository.findById(request.getParentId())
                        .orElseThrow(() -> new BizException(ErrorCode.NOT_FOUND_PARENT_PROJECT_COMMENT)) : null;

        ProjectComment projectComment = ProjectComment.builder()
                .project(project)
                .writer(writer)
                .content(request.getContent())
                .parent(parent)
                .build();
        projectCommentRepository.save(projectComment);
    }

    @Transactional
    public void updateProjectComment(final ProjectCommentUpdateRequest request, final long projectId, final long commentId) {
        ProjectComment projectComment = projectCommentRepository.findById(commentId)
                .orElseThrow(() -> new BizException(ErrorCode.NOT_FOUND_PROJECT_COMMENT));
        if (!projectComment.getProject().getId().equals(projectId)) {
            throw new BizException(ErrorCode.REQUEST_VALUE_INVALID);
        }
        projectComment.modify(request.getContent());
    }

    @Transactional
    public void deleteProjectComment(final long projectId, final long commentId) {
        ProjectComment projectComment = projectCommentRepository.findById(commentId)
                .orElseThrow(() -> new BizException(ErrorCode.NOT_FOUND_PROJECT_COMMENT));
        if (!projectComment.getProject().getId().equals(projectId)) {
            throw new BizException(ErrorCode.REQUEST_VALUE_INVALID);
        }
        projectComment.delete();
    }

    @Transactional(readOnly = true)
    public ProjectCommentListResponse readProjectCommentList(final long projectId) {
        List<ProjectComment> projectComments = projectCommentRepository.findWithAllByProjectId(projectId).stream()
                .filter(comment -> comment.getParent() == null)
                .toList();

        List<ProjectCommentDto> projectCommentDtos = projectComments.stream()
                .map(ProjectCommentModelConverter::toProjectCommentDto)
                .toList();

        return ProjectCommentListResponse.builder()
                .projectId(projectId)
                .comments(projectCommentDtos)
                .build();
    }
}
