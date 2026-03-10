package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

  @Mapping(source = "user.id", target = "id")
  @Mapping(source = "user.name", target = "userName")
  @Mapping(source = "user.email", target = "email")
  @Mapping(source = "status.online", target = "online")
  @Mapping(source = "status.lastActiveAt", target = "lastSeenAt")
  @Mapping(source = "profileImage.id", target = "profileImageId")
  UserResponse toResponse(User user, UserStatus status, BinaryContent profileImage);

  @Mapping(source = "user.id", target = "id")
  @Mapping(source = "user.name", target = "username")
  @Mapping(source = "user.email", target = "email")
  @Mapping(source = "status.online", target = "online")
  @Mapping(source = "profile", target = "profile")
  UserDto toDto(User user, UserStatus status, BinaryContentDto profile);
}