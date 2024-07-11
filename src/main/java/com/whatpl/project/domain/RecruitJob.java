package com.whatpl.project.domain;

import com.whatpl.global.common.domain.enums.Job;
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

    @Enumerated(EnumType.STRING)
    private Job job;

    private Integer recruitAmount;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    @Builder
    public RecruitJob(Job job, int recruitAmount) {
        if(recruitAmount < 1 || recruitAmount > 5) {
            throw new IllegalArgumentException("recruitAmount must be between 1 and 5");
        }
        this.job = job;
        this.recruitAmount = recruitAmount;
    }

    public void changeRecruitAmount(int recruitAmount) {
        if(recruitAmount < 1 || recruitAmount > 5) {
            throw new IllegalArgumentException("recruitAmount must be between 1 and 5");
        }
        this.recruitAmount = recruitAmount;
    }
}
