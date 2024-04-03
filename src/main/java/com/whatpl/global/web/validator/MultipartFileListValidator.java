package com.whatpl.global.web.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public class MultipartFileListValidator implements ConstraintValidator<ValidFileList, List<MultipartFile>> {

    @Override
    public boolean isValid(List<MultipartFile> multipartFileList, ConstraintValidatorContext context) {
        if (CollectionUtils.isEmpty(multipartFileList)) {
            return true;
        }
        MultipartFileValidator multipartFileValidator = new MultipartFileValidator();
        for (MultipartFile multipartFile : multipartFileList) {
            if (!multipartFileValidator.isValid(multipartFile, context)) {
                return false;
            }
        }
        return true;
    }
}
