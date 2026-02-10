package com.sprint.mission.discodeit.exception;

import com.sprint.mission.discodeit.dto.data.ErrorResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice//모든 Controller에서 발생하는 예외를 전역적으로 처리하는 클래스
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)//UserNotFoundException이 발생하면 이 메서드가 실행됨.
    @ResponseBody //반환값을 JSON으로 담음
    public ResponseEntity<ErrorResponseDto> handleUserNotFound(UserNotFoundException e) {//발생한 예외 객체를 파라미터로 받는다.
        ErrorResponseDto error = new ErrorResponseDto("USER_NOT_FOUND", e.getMessage()); //에러 응답 객체 생성
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);//상태코드 404(NOT FOUND)
    }

    @ExceptionHandler(ChannelNotFoundException.class)
    @ResponseBody
    public ResponseEntity<ErrorResponseDto> handleChannelNotFound(ChannelNotFoundException e) {
        ErrorResponseDto error = new ErrorResponseDto("CHANNEL_NOT_FOUND", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(MessageNotFoundException.class)
    @ResponseBody
    public ResponseEntity<ErrorResponseDto> handleChannelNotFound(MessageNotFoundException e) {
        ErrorResponseDto error = new ErrorResponseDto("MESSAGE_NOT_FOUND", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

}
