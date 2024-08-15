package com.whatpl.domain.project.domain;

import com.whatpl.global.common.model.BaseTimeEntity;
import com.whatpl.global.common.model.Skill;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Getter
@Entity
@Table(name = "project_skill")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProjectSkill extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private Skill skill;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    public ProjectSkill(Skill skill) {
        this.skill = skill;
    }

    public void addRelation(@NonNull Project project) {
        this.project = project;
    }
}
