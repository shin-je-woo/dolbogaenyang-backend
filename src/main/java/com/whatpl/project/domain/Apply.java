package com.whatpl.project.domain;

import com.whatpl.global.common.domain.enums.Job;
import com.whatpl.member.domain.Member;
import com.whatpl.project.domain.enums.ApplyStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "apply")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Apply {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private Job job;

    private String content;

    @Enumerated(EnumType.STRING)
    private ApplyStatus status;

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
}
