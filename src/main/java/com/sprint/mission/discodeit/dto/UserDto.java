package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.User;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;

/**
 * DTO for {@link User}
 */
@Builder
public record UserDto(UUID id, LocalDateTime createdAt, LocalDateTime updatedAt,
                      String username, String email,
                      BinaryContentDto profile,
                      boolean online) implements Serializable {

}
