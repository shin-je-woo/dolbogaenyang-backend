package com.whatpl.project.converter;

import com.whatpl.project.domain.Apply;
import com.whatpl.project.dto.ProjectApplyReadResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ApplyModelConverter {

    public static ProjectApplyReadResponse convert(Apply apply) {
        return ProjectApplyReadResponse.builder()
                .projectId(apply.getProject().getId())
                .applyId(apply.getId())
                .applicantId(apply.getApplicant().getId())
                .applicantNickname(apply.getApplicant().getNickname())
                .status(apply.getStatus())
                .job(apply.getJob())
                .content(apply.getContent())
                .recruiterReadAt(apply.getRecruiterReadAt())
                .build();
    }
}
