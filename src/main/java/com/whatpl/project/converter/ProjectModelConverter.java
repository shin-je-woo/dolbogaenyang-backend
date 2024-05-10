package com.whatpl.project.converter;

import com.whatpl.attachment.domain.Attachment;
import com.whatpl.global.common.domain.enums.Job;
import com.whatpl.member.domain.Member;
import com.whatpl.project.domain.*;
import com.whatpl.project.domain.enums.ProjectStatus;
import com.whatpl.project.dto.ProjectCreateRequest;
import com.whatpl.project.dto.ProjectJobParticipantDto;
import com.whatpl.project.dto.ProjectReadResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ProjectModelConverter {

    public static Project toProject(final ProjectCreateRequest request, final Member writer, final Attachment representImage) {
        if (request == null || writer == null || representImage == null) {
            throw new IllegalStateException("convert failed!");
        }
        Project project = Project.builder()
                .title(request.getTitle())
                .profitable(request.getProfitable())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
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

    public static Project toProject(final ProjectCreateRequest request, final Member writer) {
        if (request == null || writer == null) {
            throw new IllegalStateException("convert failed!");
        }
        Project project = Project.builder()
                .title(request.getTitle())
                .profitable(request.getProfitable())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
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
        project.addRepresentImageAndWriter(null, writer);

        return project;
    }

    public static ProjectReadResponse toProjectReadResponse(Project project, List<ProjectParticipant> projectParticipants, long likes) {
        return ProjectReadResponse.builder()
                .projectId(project.getId())
                .title(project.getTitle())
                .projectStatus(project.getStatus())
                .subject(project.getSubject())
                .meetingType(project.getMeetingType())
                .views(project.getViews())
                .likes(likes)
                .profitable(project.getProfitable())
                .writerNickname(project.getWriter().getNickname())
                .createdAt(project.getCreatedAt())
                .content(project.getContent())
                .skills(project.getProjectSkills().stream()
                        .map(ProjectSkill::getSkill)
                        .toList())
                .startDate(project.getStartDate())
                .endDate(project.getEndDate())
                .projectJobParticipants(project.getRecruitJobs().stream()
                        .map(recruitJob -> ProjectJobParticipantDto.builder()
                                .job(recruitJob.getJob())
                                .recruitAmount(recruitJob.getRecruitAmount())
                                .participantAmount(countParticipants(projectParticipants, recruitJob))
                                .participants(getJobMatchedParticipants(recruitJob.getJob(), projectParticipants))
                                .build()
                        )
                        .toList())
                .build();
    }

    private static int countParticipants(List<ProjectParticipant> projectParticipants, RecruitJob recruitJob) {
        return Long.valueOf(projectParticipants.stream()
                .filter(participant -> recruitJob.getJob().equals(participant.getJob()))
                .count()).intValue();
    }

    private static List<ProjectJobParticipantDto.ParticipantDto> getJobMatchedParticipants(Job job, List<ProjectParticipant> projectParticipants) {
        if (job == null || projectParticipants == null || projectParticipants.isEmpty()) {
            return Collections.emptyList();
        }
        return projectParticipants.stream()
                .filter(projectParticipant -> projectParticipant.getJob().equals(job))
                .map(projectParticipant -> ProjectJobParticipantDto.ParticipantDto.builder()
                        .memberId(projectParticipant.getParticipant().getId())
                        .nickname(projectParticipant.getParticipant().getNickname())
                        .career(projectParticipant.getParticipant().getCareer())
                        .build()
                )
                .toList();
    }
}
