package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.session.SessionRegistry;

@Mapper(componentModel = "spring", uses = {BinaryContentMapper.class})
public abstract class UserMapper {

  @Autowired
  protected SessionRegistry sessionRegistry;

  @Mapping(target = "online", expression = "java(isUserOnline(user))")
  public abstract UserDto toDto(User user);

  // 현재 유저가 로그인 상태인지 판별
  protected boolean isUserOnline(User user) {
    if (sessionRegistry == null) {
      return false;
    }
    // 현재 로그인된 사용자 객체들 순회
    return sessionRegistry.getAllPrincipals().stream()
        .filter(principal -> principal instanceof DiscodeitUserDetails)
        .map(principal -> (DiscodeitUserDetails) principal)
        .anyMatch(userDetails -> userDetails.getId().equals(user.getId()));
  }
}
