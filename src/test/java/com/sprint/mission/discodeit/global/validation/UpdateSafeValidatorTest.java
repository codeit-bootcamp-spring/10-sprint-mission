package com.sprint.mission.discodeit.global.validation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.ConstraintValidatorContext.ConstraintViolationBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UpdateSafeValidatorTest {

  private UpdateSafeValidator validator;
  private ConstraintValidatorContext context;
  private ConstraintViolationBuilder builder;

  @BeforeEach
  void setUp() {
    validator = new UpdateSafeValidator();
    context = mock(ConstraintValidatorContext.class);
    builder = mock(ConstraintViolationBuilder.class);
    given(context.buildConstraintViolationWithTemplate(anyString())).willReturn(builder);
    given(builder.addConstraintViolation()).willReturn(context);
  }

  @Test
  @DisplayName("isValid - 값이 null이면 항상 true를 반환한다.")
  void isValid_null() {
    boolean result = validator.isValid(null, context);
    assertThat(result).isTrue();
  }

  @Test
  @DisplayName("isValid - allowBlank가 true일 때, 빈 문자열이면 false를 반환한다.")
  void isValid_allowBlank_empty() {
    UpdateSafe annotation = mock(UpdateSafe.class);
    given(annotation.allowBlank()).willReturn(true);
    given(annotation.message()).willReturn("default message");
    validator.initialize(annotation);

    boolean result = validator.isValid("", context);

    assertThat(result).isFalse();
    verify(context).disableDefaultConstraintViolation();
    verify(context).buildConstraintViolationWithTemplate("it can't be blank or empty");
    verify(builder).addConstraintViolation();
  }

  @Test
  @DisplayName("isValid - allowBlank가 true일 때, 정상 값이면 true를 반환한다.")
  void isValid_allowBlank_valid() {
    UpdateSafe annotation = mock(UpdateSafe.class);
    given(annotation.allowBlank()).willReturn(true);
    given(annotation.message()).willReturn("default message");
    validator.initialize(annotation);

    boolean result = validator.isValid("valid string", context);

    assertThat(result).isTrue();
  }

  @Test
  @DisplayName("isValid - allowBlank가 false일 때, 공백이 포함되면 false를 반환한다.")
  void isValid_notAllowBlank_containsSpace() {
    UpdateSafe annotation = mock(UpdateSafe.class);
    given(annotation.allowBlank()).willReturn(false);
    given(annotation.message()).willReturn("default message");
    validator.initialize(annotation);

    boolean result = validator.isValid("invalid string", context);

    assertThat(result).isFalse();
  }

  @Test
  @DisplayName("isValid - allowBlank가 false일 때, 공백이 없으면 true를 반환한다.")
  void isValid_notAllowBlank_valid() {
    UpdateSafe annotation = mock(UpdateSafe.class);
    given(annotation.allowBlank()).willReturn(false);
    given(annotation.message()).willReturn("default message");
    validator.initialize(annotation);

    boolean result = validator.isValid("valid_string", context);

    assertThat(result).isTrue();
  }
}
