package com.whatpl.domain.project.dto;

import com.whatpl.global.common.model.Skill;
import com.whatpl.global.common.model.Subject;
import com.whatpl.global.pagination.SliceElement;
import com.whatpl.domain.project.model.ProjectStatus;
import lombok.*;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectInfo implements SliceElement {
    private long projectId;
    private String title;
    private ProjectStatus status;
    private Subject subject;
    private boolean myLike;
    @Setter
    private List<Skill> skills;
    @Setter
    private List<RemainedJobDto> remainedJobs;
    private boolean profitable;
    private int views;
    @Setter
    private int likes;
    @Setter
    private int comments;
    private String representImageUri;
}
