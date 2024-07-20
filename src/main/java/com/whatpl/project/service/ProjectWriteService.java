package com.whatpl.project.service;

import com.whatpl.attachment.domain.Attachment;
import com.whatpl.attachment.repository.AttachmentRepository;
import com.whatpl.global.aop.annotation.DistributedLock;
import com.whatpl.global.exception.BizException;
import com.whatpl.global.exception.ErrorCode;
import com.whatpl.global.util.FileUtils;
import com.whatpl.member.domain.Member;
import com.whatpl.member.repository.MemberRepository;
import com.whatpl.project.converter.ProjectModelConverter;
import com.whatpl.project.domain.Project;
import com.whatpl.project.dto.ProjectCreateRequest;
import com.whatpl.project.dto.ProjectUpdateRequest;
import com.whatpl.project.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProjectWriteService {

    private final MemberRepository memberRepository;
    private final AttachmentRepository attachmentRepository;
    private final ProjectRepository projectRepository;

    @Transactional
    public Long createProject(final ProjectCreateRequest request, final Long memberId) {
        Member writer = memberRepository.findById(memberId)
                .orElseThrow(() -> new BizException(ErrorCode.NOT_FOUND_MEMBER));

        Attachment representImage = getRepresentImage(request.getRepresentImageId());
        Project project = ProjectModelConverter.toProject(request, writer, representImage);

        Project savedProject = projectRepository.save(project);
        return savedProject.getId();
    }

    @Transactional
    @DistributedLock(name = "'project:'.concat(#projectId)")
    public void modifyProject(final Long projectId, final ProjectUpdateRequest request) {
        Attachment representImage = getRepresentImage(request.getRepresentImageId());
        Project project = projectRepository.findWithRecruitJobsById(projectId)
                .orElseThrow(() -> new BizException(ErrorCode.NOT_FOUND_PROJECT));
        project.modify(request, representImage);
    }

    private Attachment getRepresentImage(final Long representImageId) {
        if (representImageId == null) {
            return null;
        }
        Attachment representImage = attachmentRepository.findById(representImageId)
                .orElseThrow(() -> new BizException(ErrorCode.NOT_FOUND_FILE));
        // 프로젝트 대표이미지는 이미지 파일만 가능 (web validation 단계에서 image, pdf 가 넘어오기 때문에 한번 더 체크)
        FileUtils.validateImageFile(representImage.getMimeType());
        return representImage;
    }

    @Transactional
    @DistributedLock(name = "'project:'.concat(#projectId)")
    public void deleteProject(final Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new BizException(ErrorCode.NOT_FOUND_PROJECT));
        projectRepository.delete(project);
    }
}
