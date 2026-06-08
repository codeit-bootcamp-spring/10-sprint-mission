package com.sprint.mission.discodeit.global.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UpdateSafeValidator.class)
public @interface UpdateSafe {

  String message() default "it can't contain blank or be empty";

  boolean allowBlank() default false;

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
