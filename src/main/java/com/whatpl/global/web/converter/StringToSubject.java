package com.whatpl.global.web.converter;

import com.whatpl.global.common.model.Subject;
import jakarta.annotation.Nonnull;
import org.springframework.core.convert.converter.Converter;

public class StringToSubject implements Converter<String, Subject> {

    @Override
    public Subject convert(@Nonnull String source) {
        return Subject.from(source);
    }
}
