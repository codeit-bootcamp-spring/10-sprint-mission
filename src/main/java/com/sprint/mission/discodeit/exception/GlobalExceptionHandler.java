package com.sprint.mission.discodeit.exception;

import java.util.NoSuchElementException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
	@ExceptionHandler(NoSuchElementException.class)
	public ResponseEntity<ErrorResponse> handleNoSuchElement(NoSuchElementException e) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
			.body(new ErrorResponse("NO_SUCH_ELEMENT", e.getMessage()));
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException e,
		HttpServletRequest request,
		Model model) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
			.body(new ErrorResponse("ILLEGAL_ARGUMENT", e.getMessage()));
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleException(Exception e,
		HttpServletRequest request,
		Model model) {
		log.error("Exception 발생", e); // 예외 정보 로깅
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
			.body(new ErrorResponse("EXCEPTION", e.getMessage()));
	}

}
