package com.whatpl.project.domain;

import com.whatpl.global.common.BaseTimeEntity;
import com.whatpl.global.common.domain.enums.Job;
import com.whatpl.global.exception.BizException;
import com.whatpl.global.exception.ErrorCode;
import com.whatpl.member.domain.Member;
import com.whatpl.project.domain.enums.ApplyStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "apply")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Apply extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private Job job;

    private String content;

    @Enumerated(EnumType.STRING)
    private ApplyStatus status;

    @Setter
    private LocalDateTime recruiterReadAt;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "applicant_id")
    private Member applicant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    @Builder
    public Apply(Job job, String content, ApplyStatus status, Member applicant, Project project) {
        this.job = job;
        this.content = content;
        this.status = status;
        this.applicant = applicant;
        this.project = project;
    }

    public static Apply of(Job job, String content, Member applicant, Project project) {
        return Apply.builder()
                .job(job)
                .content(content)
                .status(ApplyStatus.WAITING)
                .applicant(applicant)
                .project(project)
                .build();
    }

    //==비즈니스 로직==//
    public void changeStatus(ApplyStatus status) {
        if (!ApplyStatus.WAITING.equals(getStatus())) {
            // 이미 처리된 지원서는 수정 불가
            throw new BizException(ErrorCode.ALREADY_PROCESSED_APPLY);
        }
        this.status = status;
    }
}
