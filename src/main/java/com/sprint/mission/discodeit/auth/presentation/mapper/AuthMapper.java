package com.sprint.mission.discodeit.auth.presentation.mapper;

import com.sprint.mission.discodeit.auth.presentation.dto.JwtLoginResponse;
import com.sprint.mission.discodeit.common.mapper.config.GlobalMapperConfig;
import com.sprint.mission.discodeit.user.domain.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = GlobalMapperConfig.class)
public interface AuthMapper {

  @Mapping(target = "userDto", source = "user")
  JwtLoginResponse toDtoFrom(User user, String accessToken, String refreshToken);
}
