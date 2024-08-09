package com.whatpl.domain.attachment.dto;

import lombok.Builder;
import lombok.Getter;
import org.springframework.core.io.Resource;

@Getter
public class ResourceDto {

    private final String fileName;
    private final Resource resource;
    private final String mimeType;

    @Builder
    public ResourceDto(String fileName, Resource resource, String mimeType) {
        this.fileName = fileName;
        this.resource = resource;
        this.mimeType = mimeType;
    }
}
