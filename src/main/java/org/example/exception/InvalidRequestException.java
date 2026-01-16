package org.example.exception;

public class InvalidRequestException extends RuntimeException {

  public InvalidRequestException(String field, String condition, Object value) {
    super(String.format("필드: %s, 조건: %s, 값: %s", field, condition, value));
  }
}
