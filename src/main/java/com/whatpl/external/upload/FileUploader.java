package com.whatpl.external.upload;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface FileUploader {
    String upload(MultipartFile multipartFile);
    Resource download(String key);
    void delete(String key);
}