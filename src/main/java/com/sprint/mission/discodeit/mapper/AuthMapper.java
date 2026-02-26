package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.LoginResponseDto;
import com.sprint.mission.discodeit.entity.User;
import org.springframework.stereotype.Component;

@Component
public class AuthMapper {

  public LoginResponseDto userToResponseDto(User user) {
    return new LoginResponseDto(
        user.getId(),
        user.getCreatedAt(),
        user.getUpdatedAt(),
        user.getUsername(),
        user.getEmail(),
        user.getPassword(),
        user.getProfileId()
    );
  }

}
