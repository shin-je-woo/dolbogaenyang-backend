package com.whatpl.domain.member.controller;

import com.whatpl.domain.member.service.MemberProjectService;
import com.whatpl.domain.project.dto.ParticipatedProject;
import com.whatpl.domain.project.dto.ParticipatedProjectResponse;
import com.whatpl.domain.project.dto.ProjectInfo;
import com.whatpl.domain.project.dto.RecruitedProjectResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MemberProjectController {

    private final MemberProjectService memberProjectService;

    @GetMapping("/members/{memberId}/projects/participated")
    public ResponseEntity<ParticipatedProjectResponse> readParticipatedProject(@PathVariable Long memberId) {
        List<ParticipatedProject> participatedProjects = memberProjectService.readParticipatedProjects(memberId);
        return ResponseEntity.ok(ParticipatedProjectResponse.from(participatedProjects));
    }

    @GetMapping("/members/{memberId}/projects/recruited")
    public ResponseEntity<RecruitedProjectResponse> readRecruitedProject(@PathVariable Long memberId) {
        List<ProjectInfo> recruitedProjects = memberProjectService.readRecruitedProjects(memberId);
        return ResponseEntity.ok(RecruitedProjectResponse.from(recruitedProjects));
    }
}
