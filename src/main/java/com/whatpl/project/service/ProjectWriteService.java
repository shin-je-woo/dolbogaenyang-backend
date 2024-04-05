package com.whatpl.project.service;

import com.whatpl.attachment.domain.Attachment;
import com.whatpl.attachment.repository.AttachmentRepository;
import com.whatpl.global.exception.BizException;
import com.whatpl.global.exception.ErrorCode;
import com.whatpl.member.domain.Member;
import com.whatpl.member.repository.MemberRepository;
import com.whatpl.project.converter.ProjectModelConverter;
import com.whatpl.project.domain.Project;
import com.whatpl.project.dto.ProjectCreateRequest;
import com.whatpl.project.repository.ProjectRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

        Attachment representImage = attachmentRepository.findById(request.getRepresentId())
                .orElseThrow(() -> new BizException(ErrorCode.NOT_FOUND_FILE));

        Project project = ProjectModelConverter.convert(request, writer, representImage);

        Project savedProject = projectRepository.save(project);
        return savedProject.getId();
    }
}
