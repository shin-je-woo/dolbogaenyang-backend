package com.whatpl.global.upload.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;

@Getter
@RequiredArgsConstructor
public enum DefaultImage {

    PROJECT("project", "DEFAULT_PROJECT_REPRESENT_IMAGE", MediaType.IMAGE_PNG_VALUE);

    private final String type;
    private final String fileName;
    private final String mimeType;
}
