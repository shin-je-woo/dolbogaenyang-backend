package com.whatpl.global.config;

import com.whatpl.global.web.converter.StringToJob;
import com.whatpl.global.web.converter.StringToProjectStatus;
import com.whatpl.global.web.converter.StringToSkill;
import com.whatpl.global.web.converter.StringToSubject;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Swagger 리소스 (build task 수행 시 리소스 생성)
        registry.addResourceHandler("/docs/**")
                .addResourceLocations("classpath:/static/docs/");
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new StringToSubject());
        registry.addConverter(new StringToSkill());
        registry.addConverter(new StringToJob());
        registry.addConverter(new StringToProjectStatus());
    }
}
