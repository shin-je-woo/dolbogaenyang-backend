package com.whatpl.global.upload;

import com.whatpl.global.upload.enums.DefaultImage;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class DefaultImageController {

    private final S3Uploader s3Uploader;

    /**
     * 디폴트 이미지 조회 API
     *
     * @param defaultImage DefaultImageArgumentResolver 에서 바인딩 되는 변수 (쿼리파라미터 type 필수)
     */
    @GetMapping("/images/default")
    public ResponseEntity<Resource> defaultImage(DefaultImage defaultImage) {
        Resource resource = s3Uploader.download(defaultImage.getFileName());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, defaultImage.getMimeType())
                .body(resource);
    }
}
