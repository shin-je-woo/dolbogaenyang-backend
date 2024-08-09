package com.whatpl.domain.project.dto;

import com.whatpl.global.common.domain.enums.Skill;
import com.whatpl.global.common.domain.enums.Subject;
import com.whatpl.domain.project.domain.enums.MeetingType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.validator.constraints.Range;

import java.util.Set;

@Getter
@Builder
@AllArgsConstructor
public class ProjectUpdateRequest {

    @NotBlank(message = "제목은 필수 입력 항목입니다.")
    @Size(max = 30, message = "제목은 최대 30자까지 입력 가능합니다.")
    private String title;

    @NotNull(message = "프로젝트의 도메인은 필수 입력 항목입니다.")
    private Subject subject;

    @Valid
    @NotNull(message = "모집직군은 필수 입력 항목입니다.")
    @Size(min = 1, message = "모집직군은 1개 이상 입력 가능합니다. ")
    private Set<RecruitJobField> recruitJobs;

    @NotNull(message = "기술 스택은 필수 입력 항목입니다.")
    @Size(min = 1, max = 15, message = "기술스택은 1 ~ 15까지 입력 가능합니다.")
    private Set<Skill> skills;

    @NotBlank(message = "프로젝트 설명은 필수 입력 항목입니다.")
    private String content;

    @NotNull(message = "수익화 여부는 필수 입력 항목입니다.")
    private Boolean profitable;

    @NotNull(message = "모임 방식은 필수 입력 항목입니다.")
    private MeetingType meetingType;

    @NotNull(message = "기간은 필수 입력 항목입니다.")
    @Range(min = 1, max = 30, message = "기간은 1 ~ 30까지 입력 가능합니다.")
    private Integer term;

    private Long representImageId;
}
