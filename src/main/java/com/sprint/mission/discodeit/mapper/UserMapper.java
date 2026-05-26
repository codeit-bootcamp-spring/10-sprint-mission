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

  @Mapping(target = "online", expression = "java(isUserOnline(user))")
  public abstract UserDto toDto(User user);

  // 현재 유저가 로그인 상태인지 판별
  protected boolean isUserOnline(User user) {
    // [임시 조치] 현재 완벽한 무상태로 전환되었으므로 서버 단독으로는 접속 여부를 알 수 없어 항상 false 반환하도록 함
    // 향후 JwtRegistry 활용하여 접속 여부 판별 로직 구현 예정
    return false;
  }
}
