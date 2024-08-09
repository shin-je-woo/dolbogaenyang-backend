package com.whatpl.global.security.model;

import com.whatpl.global.common.model.Career;
import com.whatpl.global.common.model.Job;
import org.springframework.core.annotation.AliasFor;
import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockWhatplMemberSecurityContextFactory.class)
public @interface WithMockWhatplMember {

    @AliasFor("value")
    long id() default 1L;

    boolean hasProfile() default true;

    Job job() default Job.BACKEND_DEVELOPER;

    Career career() default Career.FIVE;

    @AliasFor("id")
    long value() default 1L;
}
