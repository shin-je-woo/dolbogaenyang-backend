package com.whatpl.project.domain;

import com.whatpl.attachment.domain.Attachment;
import com.whatpl.member.domain.Career;
import com.whatpl.member.domain.Member;
import com.whatpl.member.domain.Subject;
import com.whatpl.member.domain.WorkTime;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
@Entity
@Table(name = "project")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private Boolean profitable;

    private LocalDate startDate;

    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    private ProjectStatus status;

    @Enumerated(EnumType.STRING)
    private Subject subject;

    @Enumerated(EnumType.STRING)
    private MeetingType meetingType;

    @Enumerated(EnumType.STRING)
    private Career wishCareer;

    @Enumerated(EnumType.STRING)
    private UpDown wishCareerUpDown;

    @Enumerated(EnumType.STRING)
    private WorkTime wishWorkTime;

    @Column(columnDefinition = "TEXT")
    private String content;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ProjectSkill> projectSkills = new HashSet<>();

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<RecruitJob> recruitJobs = new HashSet<>();

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "represent_image_id")
    private Attachment representImage;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_id")
    private Member writer;

    @Builder
    public Project(String title, Boolean profitable, LocalDate startDate,
                   LocalDate endDate, ProjectStatus status, Subject subject,
                   MeetingType meetingType, Career wishCareer, UpDown wishCareerUpDown,
                   WorkTime wishWorkTime, String content, Attachment representImage) {
        this.title = title;
        this.profitable = profitable;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.subject = subject;
        this.meetingType = meetingType;
        this.wishCareer = wishCareer;
        this.wishCareerUpDown = wishCareerUpDown;
        this.wishWorkTime = wishWorkTime;
        this.content = content;
        this.representImage = representImage;
    }

    //==연관관계 메서드==//
    public void addProjectSkill(ProjectSkill projectSkill) {
        if (projectSkill == null) {
            return;
        }
        this.projectSkills.add(projectSkill);
        projectSkill.setProject(this);
    }

    public void addRecruitJob(RecruitJob recruitJob) {
        if (recruitJob == null) {
            return;
        }
        this.recruitJobs.add(recruitJob);
        recruitJob.setProject(this);
    }

    public void addRepresentImageAndWriter(Attachment representImage, Member writer) {
        this.representImage = representImage;
        this.writer = writer;
    }
}
