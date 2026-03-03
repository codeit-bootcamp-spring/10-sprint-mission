package com.sprint.mission.discodeit.dto.user.request;

import jakarta.annotation.Nonnull;

public record UserFindRequest(@Nonnull String username, @Nonnull String password) {
}
