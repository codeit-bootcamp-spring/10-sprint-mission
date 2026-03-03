package com.sprint.mission.discodeit.common.exception.model;

import org.springframework.http.ProblemDetail;

import java.net.URI;
import java.time.LocalDateTime;

public class APIExceptionModel {
    private final ProblemDetail problemDetail;

    private APIExceptionModel(int status) {
        this.problemDetail = ProblemDetail.forStatus(status);
        this.problemDetail.setProperty("timestamp", LocalDateTime.now());
    }

    public static APIExceptionModel builder(int status) {
        // default setting ?
        return new APIExceptionModel(status);
    }

    public APIExceptionModel detail(String detail) {
        problemDetail.setDetail(detail);
        return this;
    }

    public APIExceptionModel uri(String uri) {
        problemDetail.setInstance(URI.create(uri));
        return this;
    }

    public APIExceptionModel method(String method) {
        problemDetail.setProperty("method", method);
        return this;
    }

    public APIExceptionModel errorCode(String errorCode) {
        problemDetail.setProperty("errorCode", errorCode);
        return this;
    }

    public ProblemDetail build() {
        return this.problemDetail;
    }
}