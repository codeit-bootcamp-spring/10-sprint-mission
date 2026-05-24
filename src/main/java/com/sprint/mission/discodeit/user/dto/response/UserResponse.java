package com.sprint.mission.discodeit.user.dto.response;

import com.sprint.mission.discodeit.binarycontent.dto.response.BinaryContentDto;
import com.sprint.mission.discodeit.user.entity.User;
import com.sprint.mission.discodeit.user.entity.constant.UserRole;
import java.io.Serializable;
import java.util.UUID;
import lombok.Builder;

/**
 * DTO for {@link User}
 */
@Builder
public record UserResponse(UUID id,
                           String username,
                           String email,
                           BinaryContentDto profile,
                           boolean online,
                           UserRole role) implements Serializable {

}
