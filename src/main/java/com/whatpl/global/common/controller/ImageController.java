package com.whatpl.global.common.controller;

import com.whatpl.global.common.model.Skill;
import com.whatpl.external.upload.FileUploader;
import com.whatpl.global.common.properties.ImageProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ImageController {

    private final FileUploader fileUploader;
    private final ImageProperties imageProperties;

    @GetMapping("/images/project")
    public ResponseEntity<Resource> projectImage() {
        Resource resource = fileUploader.download(imageProperties.getPrefix().getProject());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.IMAGE_PNG_VALUE)
                .cacheControl(CacheControl.maxAge(imageProperties.getCacheDays()).mustRevalidate())
                .body(resource);
    }

    @GetMapping("/images/skill/{skillName}")
    public ResponseEntity<Resource> skillImage(@PathVariable(name = "skillName") Skill skill) {
        Resource resource = fileUploader.download(String.format("%s_%s", imageProperties.getPrefix().getSkill(), skill.name().toUpperCase()));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "image/svg+xml")
                .cacheControl(CacheControl.maxAge(imageProperties.getCacheDays()).mustRevalidate())
                .body(resource);
    }
}
