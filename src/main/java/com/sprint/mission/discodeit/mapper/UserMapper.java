package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.UserPostDto;
import com.sprint.mission.discodeit.dto.UserResponseDto;
import com.sprint.mission.discodeit.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

  public User toUser(UserPostDto userPostDto) {
    return new User(
        userPostDto.nickName(),
        userPostDto.username(),
        userPostDto.email(),
        userPostDto.phoneNumber(),
        userPostDto.password()
    );
  }

  public UserResponseDto toUserResponseDto(User user, boolean online) {
    return new UserResponseDto(
        user.getId(),
        user.getCreatedAt(),
        user.getUpdatedAt(),
        user.getUsername(),
        user.getEmail(),
        user.getProfileId(),
        online
    );
  }
}
