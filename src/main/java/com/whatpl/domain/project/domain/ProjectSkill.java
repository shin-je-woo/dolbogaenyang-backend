package com.whatpl.domain.project.domain;

import com.whatpl.global.common.domain.BaseTimeEntity;
import com.whatpl.global.common.domain.enums.Skill;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    public ProjectSkill(Skill skill) {
        this.skill = skill;
    }
}
