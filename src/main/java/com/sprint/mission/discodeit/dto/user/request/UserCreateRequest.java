package com.sprint.mission.discodeit.dto.user.request;

import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.Email;

public record UserCreateRequest(@Nonnull String username,
                                @Nonnull @Email String email,
                                @Nonnull String password) {
}
