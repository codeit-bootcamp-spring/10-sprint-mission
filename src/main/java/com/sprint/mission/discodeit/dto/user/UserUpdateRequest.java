package com.sprint.mission.discodeit.dto.user;

import java.util.Optional;
import java.util.UUID;

public record UserUpdateRequest(
        UUID userId,
        Optional<String> userName,
        Optional<String> email,
        Optional<String> password,
        Optional<ProfileImageCreateRequest> profileImage
) {}
