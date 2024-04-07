package com.whatpl.project.converter;

import com.whatpl.attachment.domain.Attachment;
import com.whatpl.member.domain.Member;
import com.whatpl.project.domain.Project;
import com.whatpl.project.domain.ProjectSkill;
import com.whatpl.project.domain.enums.ProjectStatus;
import com.whatpl.project.domain.RecruitJob;
import com.whatpl.project.dto.ProjectCreateRequest;

import java.util.Collections;
import java.util.Optional;

public final class ProjectModelConverter {

    private ProjectModelConverter() {
    }

    public static Project convert(final ProjectCreateRequest request, final Member writer, final Attachment representImage) {
        if(request == null || writer == null || representImage == null) {
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
                .wishCareer(request.getWishCareer())
                .wishCareerUpDown(request.getWishCareerUpDown())
                .wishWorkTime(request.getWishWorkTime())
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
                        .totalCount(recruitJobField.getTotalCount())
                        .currentCount(0)
                        .build())
                .forEach(project::addRecruitJob);

        // 대표이미지, 작성자 추가
        project.addRepresentImageAndWriter(representImage, writer);

        return project;
    }
}
