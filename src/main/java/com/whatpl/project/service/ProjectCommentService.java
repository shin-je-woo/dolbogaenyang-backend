package com.whatpl.project.service;

import com.whatpl.global.exception.BizException;
import com.whatpl.global.exception.ErrorCode;
import com.whatpl.member.domain.Member;
import com.whatpl.member.repository.MemberRepository;
import com.whatpl.project.domain.Project;
import com.whatpl.project.domain.ProjectComment;
import com.whatpl.project.dto.ProjectCommentCreateRequest;
import com.whatpl.project.repository.ProjectCommentRepository;
import com.whatpl.project.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProjectCommentService {

    private final ProjectRepository projectRepository;
    private final MemberRepository memberRepository;
    private final ProjectCommentRepository projectCommentRepository;

    @Transactional
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
}
