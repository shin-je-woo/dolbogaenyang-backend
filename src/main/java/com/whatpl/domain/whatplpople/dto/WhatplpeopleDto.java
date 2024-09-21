package com.whatpl.domain.whatplpople.dto;

import com.whatpl.global.common.model.Career;
import com.whatpl.global.common.model.Job;
import com.whatpl.global.common.model.Skill;
import com.whatpl.global.common.model.Subject;
import com.whatpl.global.pagination.SliceElement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WhatplpeopleDto implements SliceElement {
    private long memberId;
    private String nickname;
    private Job job;
    private Career career;
    private List<Skill> memberSkills;
    private List<Subject> memberSubjects;
}
