package com.whatpl.domain.project.dto;

import com.whatpl.domain.project.model.ProjectStatus;
import com.whatpl.global.common.model.Job;
import com.whatpl.global.common.model.Skill;
import com.whatpl.global.common.model.Subject;
import com.whatpl.global.security.domain.MemberPrincipal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.util.StringUtils;

@Getter
@Builder
@AllArgsConstructor
public class ProjectSearchCondition {

    private Subject subject;
    private Job job;
    private Skill skill;
    private ProjectStatus status;
    private Boolean profitable;
    private String keyword;
    private Long memberId;
    private Long recruiterId;

    public void assignLoginMember(MemberPrincipal principal) {
        if (memberId != null) {
            return;
        }
        memberId = principal != null ? principal.getId() : Long.MIN_VALUE;
    }

    public boolean isEmpty() {
        return this.subject == null &&
                this.job == null &&
                this.skill == null &&
                this.profitable == null &&
                !StringUtils.hasText(this.keyword);
    }
}
