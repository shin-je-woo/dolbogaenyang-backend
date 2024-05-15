package com.whatpl.project.dto;

import com.whatpl.global.common.domain.enums.Skill;
import com.whatpl.global.common.domain.enums.Subject;
import com.whatpl.global.pagination.SliceElement;
import com.whatpl.project.domain.enums.ProjectStatus;
import lombok.*;

import java.util.ArrayList;
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
    @Setter
    private List<Skill> skills = new ArrayList<>();
    @Setter
    private List<RemainedJobDto> remainedJobs = new ArrayList<>();
    private boolean profitable;
    private long views;
    @Setter
    private long likes;
    @Setter
    private long comments;
    private String representImageUri;
}
