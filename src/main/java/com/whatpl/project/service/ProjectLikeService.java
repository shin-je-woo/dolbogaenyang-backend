package com.whatpl.project.service;

import com.whatpl.global.exception.BizException;
import com.whatpl.global.exception.ErrorCode;
import com.whatpl.member.domain.Member;
import com.whatpl.member.repository.MemberRepository;
import com.whatpl.project.domain.Project;
import com.whatpl.project.domain.ProjectLike;
import com.whatpl.project.repository.ProjectLikeRepository;
import com.whatpl.project.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ProjectLikeService {

    private final ProjectRepository projectRepository;
    private final MemberRepository memberRepository;
    private final ProjectLikeRepository projectLikeRepository;

    @Transactional
    public long putLike(final long projectId, final long memberId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new BizException(ErrorCode.NOT_FOUND_PROJECT));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BizException(ErrorCode.NOT_FOUND_MEMBER));
        ProjectLike projectLike = projectLikeRepository.findByProjectIdAndMemberId(projectId, memberId)
                .orElseGet(() -> ProjectLike.builder().project(project).member(member).build());

        return projectLikeRepository.save(projectLike).getId();
    }

    @Transactional
    public void deleteLike(final long projectId, final long memberId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new BizException(ErrorCode.NOT_FOUND_PROJECT));
        ProjectLike projectLike = projectLikeRepository.findByProjectIdAndMemberId(projectId, memberId)
                .orElseThrow(() -> new BizException(ErrorCode.NOT_FOUND_DATA));
        if (!Objects.equals(projectLike.getProject().getId(), project.getId())) {
            throw new BizException(ErrorCode.NOT_MATCH_PROJECT_LIKE);
        }
        projectLikeRepository.delete(projectLike);
    }
}
