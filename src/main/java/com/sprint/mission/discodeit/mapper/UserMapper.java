package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.config.GlobalMapperConfig;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.security.JwtRegistry;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(config = GlobalMapperConfig.class, uses = {BinaryContentMapper.class})
public abstract class UserMapper {

  @Autowired
  protected JwtRegistry jwtRegistry;

  @Mapping(target = "online", expression = "java(isUserOnline(user))")
  public abstract UserDto toDto(User user);

  // 현재 유저가 로그인 상태인지 판별
  protected boolean isUserOnline(User user) {
    if (jwtRegistry == null) {
      return false;
    }
    // Registry에서 현재 해당 유저의 살아있는 토큰이 있는지 확인
    return jwtRegistry.hasActiveJwtInformationByUserId(user.getId());
  }
}
