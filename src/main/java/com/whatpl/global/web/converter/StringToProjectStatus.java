package com.whatpl.global.web.converter;

import com.whatpl.domain.project.domain.enums.ProjectStatus;
import jakarta.annotation.Nonnull;
import org.springframework.core.convert.converter.Converter;

public class StringToProjectStatus implements Converter<String, ProjectStatus> {

    @Override
    public ProjectStatus convert(@Nonnull String source) {
        return ProjectStatus.from(source);
    }
}
