package com.whatpl.global.util;

import com.whatpl.global.exception.BizException;
import com.whatpl.global.exception.ErrorCode;
import org.apache.tika.Tika;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

public final class FileUtils {

    private FileUtils() {
        throw new IllegalStateException("This is Utility class!");
    }

    public static String extractMimeType(MultipartFile multipartFile) {
        try {
            InputStream inputStream = multipartFile.getInputStream();
            Tika tika = new Tika();
            return tika.detect(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void validateImageFile(String mimeType) {
        Set<String> imageMimeType = Set.of(MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE);
        if (!imageMimeType.contains(mimeType)) {
            throw new BizException(ErrorCode.NOT_IMAGE_FILE);
        }
    }
}
