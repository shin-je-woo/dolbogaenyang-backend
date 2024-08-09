package com.whatpl.global.common.controller;

import com.whatpl.global.common.domain.enums.*;
import com.whatpl.global.common.domain.enums.ApplyStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
public class GlobalDomainController {

    @GetMapping("/domains")
    public Map<String, List<String>> domains() {
        Map<String, List<String>> domainMap = new LinkedHashMap<>();

        domainMap.put("careers", getDomainValues(Career.class));
        domainMap.put("jobs", getDomainValues(Job.class));
        domainMap.put("skills", getDomainValues(Skill.class));
        domainMap.put("subjects", getDomainValues(Subject.class));
        domainMap.put("workTimes", getDomainValues(WorkTime.class));
        domainMap.put("applyStatuses", getDomainValues(ApplyStatus.class));

        return domainMap;
    }

    private List<String> getDomainValues(Class<? extends WhatplGlobalDomain> domain) {
        return Arrays.stream(domain.getEnumConstants())
                .map(WhatplGlobalDomain::getValue)
                .toList();
    }
}
