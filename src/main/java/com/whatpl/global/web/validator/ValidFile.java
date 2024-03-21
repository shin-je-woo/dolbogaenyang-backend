package com.whatpl.global.web.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value = ElementType.PARAMETER)
@Retention(value = RetentionPolicy.RUNTIME)
@Constraint(validatedBy = MultipartFileValidator.class)
public @interface ValidFile {

    String message() default "허용된 확장자가 아닙니다. [jpg, jpeg, png, gif, pdf]";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}
