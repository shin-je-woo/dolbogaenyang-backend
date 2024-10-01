package com.whatpl.domain.attachment.controller;

import com.whatpl.domain.attachment.dto.ResourceDto;
import com.whatpl.domain.project.service.ProjectReadService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/attachments")
@RequiredArgsConstructor
public class ProjectAttachmentController {

    private final ProjectReadService projectReadService;

    @GetMapping("/projects/represent-images/{representImageId}")
    public ResponseEntity<Resource> portfolio(@PathVariable Long representImageId) {
        ResourceDto resourceDto = projectReadService.readRepresentImage(representImageId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, resourceDto.getMimeType())
                .body(resourceDto.getResource());
    }
}
