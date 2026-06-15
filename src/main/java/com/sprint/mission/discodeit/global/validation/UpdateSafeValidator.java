package com.sprint.mission.discodeit.global.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class UpdateSafeValidator implements ConstraintValidator<UpdateSafe, String> {

  private boolean allowBlank;
  private String message;

  @Override
  public void initialize(UpdateSafe constraintAnnotation) {
    this.allowBlank = constraintAnnotation.allowBlank();
    this.message = constraintAnnotation.message();
    if (allowBlank) {
      this.message = "it can't be blank or empty";
    }
  }

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    if (value == null) {
      return true;
    }
    if (allowBlank) {
      return isValidIfAllowBlank(value, context);
    }
    return isValidIfNotAllowBlank(value, context);
  }

  private boolean isValidIfAllowBlank(String value, ConstraintValidatorContext context) {
    if (value.isEmpty()) {
      context.disableDefaultConstraintViolation();
      context.buildConstraintViolationWithTemplate(message)
          .addConstraintViolation();
      return false;
    }
    return true;
  }

  private boolean isValidIfNotAllowBlank(String value, ConstraintValidatorContext context) {
    return !value.contains(" ");
  }
}
