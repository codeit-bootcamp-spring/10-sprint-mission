package com.sprint.mission.discodeit.user.mapper;

import com.sprint.mission.discodeit.binarycontent.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.jwt.JwtRegistry;
import com.sprint.mission.discodeit.user.dto.UserDto;
import com.sprint.mission.discodeit.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMapper {

  private final BinaryContentMapper binaryContentMapper;
  private final JwtRegistry jwtRegistry;

  public UserDto toDto(User user) {
    return new UserDto(user.getId(),
        user.getCreatedAt(),
        user.getUpdatedAt(),
        user.getUsername(),
        user.getEmail(),
        binaryContentMapper.toDto(user.getProfile()),
        user.getRole(),
        jwtRegistry.hasActiveJwtInformationByUserId(user.getId())
    );

  }

}
