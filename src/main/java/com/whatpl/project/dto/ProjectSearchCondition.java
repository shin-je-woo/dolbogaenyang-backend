package com.whatpl.project.dto;

import com.whatpl.global.common.domain.enums.Job;
import com.whatpl.global.common.domain.enums.Skill;
import com.whatpl.global.common.domain.enums.Subject;
import com.whatpl.global.security.domain.MemberPrincipal;
import com.whatpl.project.domain.enums.ProjectStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

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
    private Long longinMemberId;

    public void assignLoginMember(MemberPrincipal principal) {
        if (longinMemberId != null) {
            return;
        }
        longinMemberId = principal != null ? principal.getId() : Long.MIN_VALUE;
    }
}
