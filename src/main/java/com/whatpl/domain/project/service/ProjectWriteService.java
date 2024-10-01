package com.whatpl.domain.project.service;

import com.whatpl.domain.attachment.domain.Attachment;
import com.whatpl.domain.attachment.repository.AttachmentRepository;
import com.whatpl.global.aop.annotation.DistributedLock;
import com.whatpl.global.exception.BizException;
import com.whatpl.global.exception.ErrorCode;
import com.whatpl.global.util.FileUtils;
import com.whatpl.domain.member.domain.Member;
import com.whatpl.domain.member.repository.MemberRepository;
import com.whatpl.domain.project.mapper.ProjectMapper;
import com.whatpl.domain.project.domain.Project;
import com.whatpl.domain.project.dto.ProjectCreateRequest;
import com.whatpl.domain.project.dto.ProjectUpdateRequest;
import com.whatpl.domain.project.repository.project.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProjectWriteService {

    private final MemberRepository memberRepository;
    private final AttachmentRepository attachmentRepository;
    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;

    @Transactional
    @CacheEvict(value = "projectList", allEntries = true)
    public Long createProject(final ProjectCreateRequest request, final Long memberId) {
        Member writer = memberRepository.findById(memberId)
                .orElseThrow(() -> new BizException(ErrorCode.NOT_FOUND_MEMBER));

        Attachment representImage = getRepresentImage(request.getRepresentImageId());
        Project project = projectMapper.toProject(request, writer, representImage);

        Project savedProject = projectRepository.save(project);
        return savedProject.getId();
    }

    @Transactional
    @DistributedLock(name = "'project:'.concat(#projectId)")
    @CacheEvict(value = "projectList", allEntries = true)
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
        FileUtils.validateImageFile(representImage.getMimeType());
        return representImage;
    }

    @Transactional
    @DistributedLock(name = "'project:'.concat(#projectId)")
    @CacheEvict(value = "projectList", allEntries = true)
    public void deleteProject(final Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new BizException(ErrorCode.NOT_FOUND_PROJECT));
        projectRepository.delete(project);
    }
}
