package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.common.mapper.config.GlobalMapperConfig;
import com.sprint.mission.discodeit.userstatus.dto.UserStatusDto;
import com.sprint.mission.discodeit.userstatus.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.userstatus.entity.UserStatus;
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
