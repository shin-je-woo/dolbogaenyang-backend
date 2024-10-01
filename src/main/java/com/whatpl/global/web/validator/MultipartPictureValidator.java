package com.whatpl.global.web.validator;

import com.whatpl.global.util.FileUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

import static org.springframework.http.MediaType.*;

public class MultipartPictureValidator implements ConstraintValidator<ValidPicture, MultipartFile> {

    private static final Set<String> allowedTypes = Set.of(IMAGE_JPEG_VALUE, IMAGE_PNG_VALUE);

    @Override
    public boolean isValid(MultipartFile multipartFile, ConstraintValidatorContext context) {
        return hasExtension(multipartFile) && isAllowedType(multipartFile);
    }

    private boolean hasExtension(MultipartFile multipartFile) {
        return StringUtils.hasText(StringUtils.getFilenameExtension(multipartFile.getOriginalFilename()));
    }

    private boolean isAllowedType(MultipartFile multipartFile) {
        String mimeType = FileUtils.extractMimeType(multipartFile);
        return allowedTypes.stream()
                .anyMatch(type -> type.equals(mimeType));
    }
}
