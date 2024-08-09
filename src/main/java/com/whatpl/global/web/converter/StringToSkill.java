package com.whatpl.global.web.converter;

import com.whatpl.global.common.model.Skill;
import jakarta.annotation.Nonnull;
import org.springframework.core.convert.converter.Converter;

public class StringToSkill implements Converter<String, Skill> {

    @Override
    public Skill convert(@Nonnull String source) {
        return Skill.from(source);
    }
}
