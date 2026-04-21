package com.sprint.mission.discodeit.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.Map;

@Getter
@AllArgsConstructor
public class ErrorResponse {
    private Instant timeStamp;
    private String code;
    private String message;
    private Map<String, Object> details;
    private String exceptionType;
    private int status;
}
