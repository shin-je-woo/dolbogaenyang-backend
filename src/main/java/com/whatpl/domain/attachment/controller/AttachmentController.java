package com.whatpl.domain.attachment.controller;

import com.whatpl.domain.attachment.dto.ResourceDto;
import com.whatpl.domain.attachment.dto.UploadResponse;
import com.whatpl.domain.attachment.service.AttachmentService;
import com.whatpl.global.web.validator.ValidFile;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;

@Validated
@Controller
@RequiredArgsConstructor
public class AttachmentController {

    private final AttachmentService attachmentService;

    @PostMapping("/attachments")
    public ResponseEntity<UploadResponse> upload(@ValidFile @RequestPart("file") MultipartFile multipartFile) {
        long id = attachmentService.uploadAndSave(multipartFile);
        return ResponseEntity.status(HttpStatus.CREATED).body(new UploadResponse(id));
    }

    @GetMapping("/attachments/{id}")
    public ResponseEntity<Resource> download(@PathVariable Long id) {
        ResourceDto resourceDto = attachmentService.download(id);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment()
                        .filename(resourceDto.getFileName(), StandardCharsets.UTF_8)
                        .build()
                        .toString())
                .body(resourceDto.getResource());
    }

    @GetMapping("/attachments/{id}/images")
    public ResponseEntity<Resource> preview(@PathVariable Long id) {
        ResourceDto resourceDto = attachmentService.download(id);
        return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, resourceDto.getMimeType())
                    .body(resourceDto.getResource());
    }
}
