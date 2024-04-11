package com.whatpl.project.repository;

import com.whatpl.project.domain.Apply;
import com.whatpl.project.domain.Project;

import java.util.List;

public interface ApplyQueryRepository {
    List<Apply> findAllParticipants(Project project);
}
