package com.sprint.mission.discodeit.dto;

import lombok.NonNull;

public interface AuthServiceDTO {
    record UserLogin(@NonNull String username, @NonNull String password) {}
}
