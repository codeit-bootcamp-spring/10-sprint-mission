package com.sprint.mission.discodeit.user.presentation.dto.response;

import com.sprint.mission.discodeit.binarycontent.presentation.dto.response.BinaryContentDto;
import com.sprint.mission.discodeit.user.domain.entity.User;
import com.sprint.mission.discodeit.user.domain.entity.constant.UserRole;
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
