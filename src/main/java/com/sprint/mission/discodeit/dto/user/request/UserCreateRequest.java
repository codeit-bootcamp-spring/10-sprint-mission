package com.sprint.mission.discodeit.dto.user.request;

import com.sprint.mission.discodeit.dto.binarycontent.request.BinaryContentCreateRequest;

public record UserCreateRequest(
        String name,
        String email,
        String password,
        BinaryContentCreateRequest profileImage
) {}
