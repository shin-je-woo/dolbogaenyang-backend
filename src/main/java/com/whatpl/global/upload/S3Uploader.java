package com.whatpl.global.upload;

import com.whatpl.global.exception.BizException;
import com.whatpl.global.exception.ErrorCode;
import io.awspring.cloud.s3.ObjectMetadata;
import io.awspring.cloud.s3.S3Resource;
import io.awspring.cloud.s3.S3Template;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Uploader {

    private final S3Template s3Template;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucket;

    public String upload(MultipartFile multipartFile) {
        String storedName = UUID.randomUUID().toString();
        try (InputStream is = multipartFile.getInputStream()) {
            s3Template.upload(bucket, storedName, is,
                    ObjectMetadata.builder().contentType(multipartFile.getContentType()).build());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return storedName;
    }

    public Resource download(String key) {
        S3Resource resource = s3Template.download(bucket, key);
        if (!resource.exists()) {
            throw new BizException(ErrorCode.NOT_FOUND_FILE);
        }
        return resource;
    }
}
