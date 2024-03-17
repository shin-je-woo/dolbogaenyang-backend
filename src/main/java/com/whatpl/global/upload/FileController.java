package com.whatpl.global.upload;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;

@Controller
@RequiredArgsConstructor
public class FileController {

    private final S3Uploader s3Uploader;

    @PostMapping("/files")
    public ResponseEntity<String> upload(@RequestParam("file") MultipartFile multipartFile) {
        String upload = s3Uploader.upload(multipartFile);
        return ResponseEntity.ok(upload);
    }

    @GetMapping("/files")
    public ResponseEntity<Resource> download(@RequestParam String key) {
        Resource resource = s3Uploader.download(key);
        // TODO filename DB 에서 가져와서 세팅하기
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment()
                        .filename("sample.pdf", StandardCharsets.UTF_8)
                        .build()
                        .toString())
                .body(resource);
    }
}
