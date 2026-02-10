package com.sprint.mission.discodeit.dto;

import lombok.NonNull;

public interface AuthServiceDTO {
    record LoginRequest(@NonNull String username, @NonNull String password) {
    }
}
