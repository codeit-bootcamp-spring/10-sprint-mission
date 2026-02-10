package com.sprint.mission.discodeit.dto.request;

import java.util.Optional;

public record UserCreateRequest(
        String username,
        String email,
        String password,
        Optional<BinaryContentCreateRequest> optionalProfileCreateRequest
) {}
