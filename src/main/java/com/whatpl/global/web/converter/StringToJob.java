package com.whatpl.global.web.converter;

import com.whatpl.global.common.domain.enums.Job;
import jakarta.annotation.Nonnull;
import org.springframework.core.convert.converter.Converter;

public class StringToJob implements Converter<String, Job> {

    @Override
    public Job convert(@Nonnull String source) {
        return Job.from(source);
    }
}
