package com.sprint.mission.discodeit.dto.user.request;

import java.util.UUID;

public record UserCreateRequest(
        String name,
        String email,
        String password,
        // BinaryContentRequest로 분리할 것, content dto를 필드로 갖는게 좋아보임
        byte[] profileImage,
        String type
) {}
