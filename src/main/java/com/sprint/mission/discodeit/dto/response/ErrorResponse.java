package com.sprint.mission.discodeit.dto.response;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.util.Map;


@Getter
public class ErrorResponse {
    private Instant timestamp;
    private String code;
    private String message;
    private Map<String, Object> details;
    private String exceptionType; //발생한 예외의 클래스 이름
    private int status; //HTTP 상태코드

    public ErrorResponse(DiscodeitException e, int status) {
        this.timestamp = e.getTimestamp();
        this.code = e.getErrorCode().name();
        this.message = e.getErrorCode().getMessage();
        this.details = e.getDetails();
        this.exceptionType = e.getClass().getSimpleName();
        this.status = status;

    }


}
