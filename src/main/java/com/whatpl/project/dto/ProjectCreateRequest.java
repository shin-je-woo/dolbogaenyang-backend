package com.whatpl.project.dto;

import com.whatpl.global.common.domain.enums.*;
import com.whatpl.project.domain.enums.MeetingType;
import com.whatpl.project.domain.enums.UpDown;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Builder
@AllArgsConstructor
public class ProjectCreateRequest {

    @NotBlank(message = "제목은 필수 입력 항목입니다.")
    @Size(max = 30, message = "제목은 최대 30자까지 입력 가능합니다.")
    private String title;

    @NotNull(message = "프로젝트의 도메인은 필수 입력 항목입니다.")
    @Size(min = 1, max = 3, message = "프로젝트 도메인은 1~3개 입력 가능합니다.")
    private Set<Subject> subjects;

    @Valid
    @NotNull(message = "모집직군은 필수 입력 항목입니다.")
    private Set<RecruitJobField> recruitJobs;

    @NotNull(message = "기술 스택은 필수 입력 항목입니다.")
    private Set<Skill> skills;

    @NotBlank(message = "프로젝트 설명은 필수 입력 항목입니다.")
    private String content;

    @NotNull(message = "수익화 여부는 필수 입력 항목입니다.")
    private Boolean profitable;

    @NotNull(message = "모임 방식은 필수 입력 항목입니다.")
    private MeetingType meetingType;

    @NotNull(message = "프로젝트 시작 일자는 필수 입력 항목입니다.")
    private LocalDate startDate;

    @NotNull(message = "프로젝트 종료 일자는 필수 입력 항목입니다.")
    private LocalDate endDate;

    private Long representId;

    private Career wishCareer;

    private UpDown wishCareerUpDown;
    
    private WorkTime wishWorkTime;

    @Getter
    @AllArgsConstructor
    public static class RecruitJobField {
        @NotNull(message = "직무는 필수 입력 항목입니다.")
        private Job job;
        @NotNull(message = "모집인원은 필수 입력 항목입니다.")
        @Min(value = 1, message = "모집인원은 최소 1명 이상 입력 가능합니다.")
        @Max(value = 10, message = "모집인원은 최소 10명 이하 입력 가능합니다.")
        private Integer totalCount;
    }
}
