package com.sprint.mission.discodeit.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorResponse {
    private String code;
    private int status;
}
