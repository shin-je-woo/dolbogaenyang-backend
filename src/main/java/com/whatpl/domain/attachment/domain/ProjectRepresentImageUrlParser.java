package com.whatpl.domain.attachment.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class ProjectRepresentImageUrlParser implements AttachmentUrlParser {

    @Value("${server-url}")
    private String serverUrl;

    @Override
    public boolean supports(AttachmentUrlParseType type) {
        return Objects.equals(type, AttachmentUrlParseType.PROJECT_REPRESENT_IMAGE);
    }

    @Override
    public String parseUrl(Long representImageId) {
        if (representImageId == null) return String.format("%s/images/project", serverUrl);
        return String.format("%s/attachments/projects/represent-images/%d", serverUrl, representImageId);
    }
}
