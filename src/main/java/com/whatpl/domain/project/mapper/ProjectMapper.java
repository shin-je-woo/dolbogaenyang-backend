package com.whatpl.domain.project.mapper;

import com.whatpl.domain.attachment.domain.Attachment;
import com.whatpl.domain.attachment.domain.AttachmentUrlParseDelegator;
import com.whatpl.domain.attachment.domain.AttachmentUrlParseType;
import com.whatpl.domain.member.domain.Member;
import com.whatpl.domain.project.domain.Project;
import com.whatpl.domain.project.domain.ProjectParticipant;
import com.whatpl.domain.project.domain.ProjectSkill;
import com.whatpl.domain.project.domain.RecruitJob;
import com.whatpl.domain.project.dto.ParticipatedProject;
import com.whatpl.domain.project.dto.ProjectCreateRequest;
import com.whatpl.domain.project.dto.ProjectJobParticipantDto;
import com.whatpl.domain.project.dto.ProjectReadResponse;
import com.whatpl.domain.project.model.ProjectStatus;
import com.whatpl.global.common.model.Job;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public final class ProjectMapper {

    private final AttachmentUrlParseDelegator attachmentUrlParseDelegator;

    public Project toProject(final ProjectCreateRequest request, final Member writer, final Attachment representImage) {
        if (request == null || writer == null) {
            throw new IllegalStateException("convert failed!");
        }
        Project project = Project.builder()
                .title(request.getTitle())
                .profitable(request.getProfitable())
                .term(request.getTerm())
                .status(ProjectStatus.RECRUITING)
                .subject(request.getSubject())
                .meetingType(request.getMeetingType())
                .content(request.getContent())
                .build();

        // ProjectSkill 추가
        Optional.ofNullable(request.getSkills())
                .orElseGet(Collections::emptySet).stream()
                .map(ProjectSkill::new)
                .forEach(project::addProjectSkill);

        // RecruitJob 추가
        Optional.ofNullable(request.getRecruitJobs())
                .orElseGet(Collections::emptySet).stream()
                .map(recruitJobField -> RecruitJob.builder()
                        .job(recruitJobField.getJob())
                        .recruitAmount(recruitJobField.getRecruitAmount())
                        .build())
                .forEach(project::addRecruitJob);

        // 대표이미지, 작성자 추가
        project.addRepresentImageAndWriter(representImage, writer);

        return project;
    }

    public ProjectReadResponse toProjectReadResponse(Project project, Long memberId) {
        return ProjectReadResponse.builder()
                .projectId(project.getId())
                .representImageUrl(buildRepresentImageUrl(project))
                .title(project.getTitle())
                .projectStatus(project.getStatus())
                .subject(project.getSubject())
                .meetingType(project.getMeetingType())
                .views(project.getViews())
                .likes(project.getProjectLikes().size())
                .profitable(project.getProfitable())
                .writerNickname(project.getWriter().getNickname())
                .createdAt(project.getCreatedAt())
                .content(project.getContent())
                .skills(project.getProjectSkills().stream()
                        .map(ProjectSkill::getSkill)
                        .toList())
                .term(project.getTerm())
                .projectJobParticipants(project.getRecruitJobs().stream()
                        .map(recruitJob -> buildJobParticipant(recruitJob, project.getProjectParticipants()))
                        .sorted(Comparator.comparingInt(recruitJob -> recruitJob.getJob().ordinal()))
                        .toList())
                .myLike(project.getProjectLikes().stream()
                        .anyMatch(projectLike -> projectLike.getMember().getId().equals(memberId)))
                .build();
    }

    private int countParticipants(List<ProjectParticipant> projectParticipants, RecruitJob recruitJob) {
        return Long.valueOf(projectParticipants.stream()
                .filter(participant -> recruitJob.getJob().equals(participant.getJob()))
                .count()).intValue();
    }

    private List<ProjectJobParticipantDto.ParticipantDto> getJobMatchedParticipants(Job job, List<ProjectParticipant> projectParticipants) {
        if (job == null || projectParticipants == null || projectParticipants.isEmpty()) {
            return Collections.emptyList();
        }
        return projectParticipants.stream()
                .filter(projectParticipant -> projectParticipant.getJob().equals(job))
                .map(this::buildParticipant)
                .toList();
    }

    private ProjectJobParticipantDto buildJobParticipant(RecruitJob recruitJob, List<ProjectParticipant> projectParticipants) {
        return ProjectJobParticipantDto.builder()
                .job(recruitJob.getJob())
                .recruitAmount(recruitJob.getRecruitAmount())
                .participantAmount(countParticipants(projectParticipants, recruitJob))
                .participants(getJobMatchedParticipants(recruitJob.getJob(), projectParticipants))
                .build();
    }

    private ProjectJobParticipantDto.ParticipantDto buildParticipant(ProjectParticipant projectParticipant) {
        return ProjectJobParticipantDto.ParticipantDto.builder()
                .participantId(projectParticipant.getId())
                .memberId(projectParticipant.getParticipant().getId())
                .job(projectParticipant.getParticipant().getJob())
                .nickname(projectParticipant.getParticipant().getNickname())
                .career(projectParticipant.getParticipant().getCareer())
                .build();
    }

    public ParticipatedProject toParticipatedProject(Member member, Project project) {
        return ParticipatedProject.builder()
                .projectId(project.getId())
                .title(project.getTitle())
                .representImageUrl(buildRepresentImageUrl(project))
                .subject(project.getSubject())
                .job(getMatchedParticipant(member, project)
                        .map(ProjectParticipant::getJob)
                        .orElse(null))
                .participatedAt(getMatchedParticipant(member, project)
                        .map(ProjectParticipant::getCreatedAt)
                        .orElse(null))
                .build();
    }

    private Optional<ProjectParticipant> getMatchedParticipant(Member member, Project project) {
        return project.getProjectParticipants().stream()
                .filter(projectParticipant -> projectParticipant.getParticipant().getId().equals(member.getId()))
                .findFirst();
    }

    private String buildRepresentImageUrl(Project project) {
        Long representImageId = project.getRepresentImage() == null ? null : project.getRepresentImage().getId();
        return attachmentUrlParseDelegator.parseUrl(AttachmentUrlParseType.PROJECT_REPRESENT_IMAGE, representImageId);
    }
}
