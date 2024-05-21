package com.whatpl.project.dto;

import com.whatpl.global.common.domain.enums.Skill;
import com.whatpl.global.common.domain.enums.Subject;
import com.whatpl.global.pagination.SliceElement;
import com.whatpl.project.domain.enums.ProjectStatus;
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
