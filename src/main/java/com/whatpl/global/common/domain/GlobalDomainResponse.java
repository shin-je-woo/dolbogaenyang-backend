package com.whatpl.global.common.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@Builder
@RequiredArgsConstructor
public class GlobalDomainResponse {
    private final List<String> careers;
    private final List<String> jobs;
    private final List<String> skills;
    private final List<String> subjects;
    private final List<String> workTimes;
    private final List<String> applyStatuses;
}
