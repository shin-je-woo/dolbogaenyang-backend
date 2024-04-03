package com.whatpl.global.util;

import org.apache.tika.Tika;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

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
}
