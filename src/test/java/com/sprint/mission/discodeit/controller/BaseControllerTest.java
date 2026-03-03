package com.sprint.mission.discodeit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

public abstract class BaseControllerTest {
    protected final String PREFIX = "/v1/api";
    protected final String BASE_URI;
    @Autowired protected MockMvc mockMvc;
    @Autowired protected ObjectMapper objectMapper;

    public BaseControllerTest(String BASE_URI) {
        this.BASE_URI = BASE_URI;
    }

    protected String getUri(String endpoint) {
        return PREFIX + BASE_URI + endpoint;
    }

    protected String getUri() {
        return getUri("");
    }
}
