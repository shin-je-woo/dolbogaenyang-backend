package com.whatpl.global.web.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.tika.Tika;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

import static org.springframework.http.MediaType.*;

public class MultipartFileValidator implements ConstraintValidator<ValidFile, MultipartFile> {

    private static final Set<String> allowedTypes = Set.of(APPLICATION_PDF_VALUE,
            IMAGE_JPEG_VALUE, IMAGE_PNG_VALUE, IMAGE_GIF_VALUE);

    @Override
    public boolean isValid(MultipartFile multipartFile, ConstraintValidatorContext context) {
        return hasExtension(multipartFile) && isAllowedType(multipartFile);
    }

    private boolean hasExtension(MultipartFile multipartFile) {
        return StringUtils.hasText(StringUtils.getFilenameExtension(multipartFile.getOriginalFilename()));
    }

    private boolean isAllowedType(MultipartFile multipartFile) {
        boolean result;
        try {
            InputStream inputStream = multipartFile.getInputStream();
            Tika tika = new Tika();
            String mimeType = tika.detect(inputStream);
            result = allowedTypes.stream()
                    .anyMatch(type -> type.equals(mimeType));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return result;
    }
}
