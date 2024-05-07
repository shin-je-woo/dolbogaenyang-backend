package com.whatpl.project.domain;

import com.whatpl.global.common.BaseTimeEntity;
import com.whatpl.global.common.domain.enums.Subject;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Entity
@Table(name = "project_subject")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProjectSubject extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private Subject subject;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    public ProjectSubject(Subject subject) {
        this.subject = subject;
    }
}
