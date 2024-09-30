package com.whatpl.domain.member.service;

import com.whatpl.domain.member.domain.Member;
import com.whatpl.domain.member.repository.MemberRepository;
import com.whatpl.domain.project.dto.ParticipatedProject;
import com.whatpl.domain.project.dto.ProjectInfo;
import com.whatpl.domain.project.service.ProjectReadService;
import com.whatpl.global.exception.BizException;
import com.whatpl.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberProjectService {

    private final MemberRepository memberRepository;
    private final ProjectReadService projectReadService;

    public List<ParticipatedProject> readParticipatedProjects(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BizException(ErrorCode.NOT_FOUND_MEMBER));
        return projectReadService.readParticipatedProject(member);
    }

    public List<ProjectInfo> readRecruitedProjects(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BizException(ErrorCode.NOT_FOUND_MEMBER));
        return projectReadService.readRecruitedProjects(member);
    }
}
