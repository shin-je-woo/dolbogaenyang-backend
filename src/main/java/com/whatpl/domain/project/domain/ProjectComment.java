package com.whatpl.domain.project.domain;

import com.whatpl.global.common.BaseTimeEntity;
import com.whatpl.domain.member.domain.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Entity
@Table(name = "project_comment")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProjectComment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;

    private Boolean modified;

    private Boolean deleted;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_id")
    private Member writer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private ProjectComment parent;

    @OneToMany(mappedBy = "parent")
    @OrderBy("createdAt ASC")
    private Set<ProjectComment> children = new LinkedHashSet<>();

    @Builder
    public ProjectComment(String content, Member writer, Project project, ProjectComment parent) {
        this.content = content;
        this.modified = false;
        this.deleted = false;
        this.writer = writer;
        this.project = project;
        this.parent = parent;
    }

    //==비즈니스 로직==//
    public void modify(String content) {
        this.content = content;
        this.modified = true;
    }

    public void delete() {
        this.deleted = true;
    }
}
