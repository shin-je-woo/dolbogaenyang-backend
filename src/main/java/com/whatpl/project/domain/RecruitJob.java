package com.whatpl.project.domain;

import com.whatpl.member.domain.Job;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Entity
@Table(name = "recruit_job")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecruitJob {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Job job;

    private Integer totalCount;

    private Integer currentCount;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    @Builder
    public RecruitJob(Job job, Integer totalCount, Integer currentCount) {
        this.job = job;
        this.totalCount = totalCount;
        this.currentCount = currentCount;
    }
}
