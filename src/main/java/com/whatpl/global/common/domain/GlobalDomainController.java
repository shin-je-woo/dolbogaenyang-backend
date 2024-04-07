package com.whatpl.global.common.domain;

import com.whatpl.global.common.domain.enums.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
public class GlobalDomainController {

    @GetMapping("/domains")
    public ResponseEntity<GlobalDomainResponse> all() {
        List<String> careers = Arrays.stream(Career.values())
                .map(Career::getValue)
                .toList();
        List<String> jobs = Arrays.stream(Job.values())
                .map(Job::getValue)
                .toList();
        List<String> skills = Arrays.stream(Skill.values())
                .map(Skill::getValue)
                .toList();
        List<String> subjects = Arrays.stream(Subject.values())
                .map(Subject::getValue)
                .toList();
        List<String> workTimes = Arrays.stream(WorkTime.values())
                .map(WorkTime::getValue)
                .toList();
        return ResponseEntity.ok(GlobalDomainResponse.builder()
                .careers(careers)
                .jobs(jobs)
                .skills(skills)
                .subjects(subjects)
                .workTimes(workTimes)
                .build());
    }
}
