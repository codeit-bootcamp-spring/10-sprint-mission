package com.sprint.mission.discodeit.exception;

import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler();

    @Test
    @DisplayName("커스텀 예외 응답 변환 성공")
    void handleApiException_returnsCustomExceptionMetadata() {
        UserNotFoundException exception = new UserNotFoundException(
                "사용자를 찾을 수 없습니다",
                Map.of("userId", "user-1")
        );

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleApiException(exception);

        assertThat(response.getStatusCode().value()).isEqualTo(404);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().exceptionType()).isEqualTo("UserNotFoundException");
        assertThat(response.getBody().details()).containsEntry("userId", "user-1");
    }

    @Test
    @DisplayName("IllegalArgumentException 변환 성공")
    void handleIllegalArgumentException_convertsToInvalidRequestException() {
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleIllegalArgumentException(
                new IllegalArgumentException("잘못된 요청입니다")
        );

        assertThat(response.getStatusCode().value()).isEqualTo(400);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code()).isEqualTo(ErrorCode.INVALID_REQUEST.getCode());
        assertThat(response.getBody().exceptionType()).isEqualTo("InvalidRequestException");
    }
}
