package com.whatpl;

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
    long id() default 0L;

    @AliasFor("id")
    long value() default 0L;
}
