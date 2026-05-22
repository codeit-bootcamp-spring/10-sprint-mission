package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

  @Mapping(source = "user.id", target = "id")
  @Mapping(source = "user.username", target = "userName")
  @Mapping(source = "user.email", target = "email")
  @Mapping(source = "online", target = "online")
  @Mapping(target = "lastSeenAt", ignore = true)
  @Mapping(source = "profileImage.id", target = "profileImageId")
  @Mapping(source = "user.role", target = "role")
  UserResponse toResponse(User user, boolean online, BinaryContent profileImage);

  @Mapping(source = "user.id", target = "id")
  @Mapping(source = "user.username", target = "username")
  @Mapping(source = "user.email", target = "email")
  @Mapping(source = "online", target = "online")
  @Mapping(source = "profile", target = "profile")
  @Mapping(source = "user.role", target = "role")
  UserDto toDto(User user, boolean online, BinaryContentDto profile);
}