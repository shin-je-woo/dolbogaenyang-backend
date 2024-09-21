package com.whatpl.domain.whatplpople.dto;

import com.whatpl.global.common.model.Career;
import com.whatpl.global.common.model.Job;
import com.whatpl.global.common.model.Skill;
import com.whatpl.global.common.model.Subject;
import com.whatpl.global.security.domain.MemberPrincipal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WhatplpeopleSearchCondition {
    private String keyword;
    private Job job;
    private Career career;
    private Skill skill;
    private List<Subject> subjects;
    private Long loginMemberId;

    public void assignLoginMember(MemberPrincipal principal) {
        if (loginMemberId != null || principal == null) {
            return;
        }
        loginMemberId =  principal.getId();
        job = principal.getJob();
        career = principal.getCareer();
    }

    public void addJob(){

    }
}
