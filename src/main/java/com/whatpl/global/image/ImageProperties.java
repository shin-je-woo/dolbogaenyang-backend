package com.whatpl.global.image;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@Getter
@AllArgsConstructor
@ConfigurationProperties(prefix = "default-image")
public class ImageProperties {

    private Duration cacheDays;
    private Prefix prefix;

    @Getter
    @AllArgsConstructor
    static class Prefix {
        private String project;
        private String skill;
    }
}
