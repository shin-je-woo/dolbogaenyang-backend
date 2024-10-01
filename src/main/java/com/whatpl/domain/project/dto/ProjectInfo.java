package com.whatpl.domain.project.dto;

import com.whatpl.global.common.model.Skill;
import com.whatpl.global.common.model.Subject;
import com.whatpl.global.pagination.SliceElement;
import com.whatpl.domain.project.model.ProjectStatus;
import lombok.*;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

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
    private List<Skill> skills;
    private List<RemainedJobDto> remainedJobs;
    private boolean profitable;
    private int views;
    private int likes;
    private int comments;
    private Long representImageId;
    private String representImageUrl;

    @Getter
    @Builder
    public static class Editor {

        private List<Skill> skills;
        private List<RemainedJobDto> remainedJobs;
        private int likes;
        private int comments;
        private String representImageUrl;

        public static Editor fromSkills(List<Skill> skills) {
            return Editor.builder().skills(skills).build();
        }

        public static Editor fromRemainedJobs(List<RemainedJobDto> remainedJobs) {
            return Editor.builder().remainedJobs(remainedJobs).build();
        }

        public static Editor fromLikes(int likes) {
            return Editor.builder().likes(likes).build();
        }

        public static Editor fromComments(int comments) {
            return Editor.builder().comments(comments).build();
        }

        public static Editor fromRepresentImageUrl(String representImageUrl) {return Editor.builder().representImageUrl(representImageUrl).build();}

        public void merge(ProjectInfo original) {
            if (!CollectionUtils.isEmpty(skills)) {
                original.skills = skills;
            }
            if (!CollectionUtils.isEmpty(remainedJobs)) {
                original.remainedJobs = remainedJobs;
            }
            if (likes > 0) {
                original.likes = likes;
            }
            if (comments > 0) {
                original.comments = comments;
            }
            if (StringUtils.hasText(representImageUrl)) {
                original.representImageUrl = representImageUrl;
            }
        }
    }
}
