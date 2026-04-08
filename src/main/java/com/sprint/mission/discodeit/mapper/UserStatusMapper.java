package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.UserStatusDto;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.config.GlobalMapperConfig;
import java.time.Instant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = GlobalMapperConfig.class)
public interface UserStatusMapper {

  @Mapping(target = "userId", source = "user.id")
  UserStatusDto toDto(UserStatus userStatus);

  UserStatus partialUpdate(UserStatusUpdateRequest request, @MappingTarget UserStatus userStatus);

  default UserStatus createDefault() {
    return UserStatus.builder().lastActiveAt(Instant.now()).build();
  }
}
