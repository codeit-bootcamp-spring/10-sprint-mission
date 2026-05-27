package com.sprint.mission.discodeit.auth;

import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.UserDto.UserCreateRequest;
import com.sprint.mission.discodeit.dto.UserDto.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.exception.user.DuplicateEmailException;
import com.sprint.mission.discodeit.exception.user.DuplicateUsernameException;
import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class InitAdmin implements ApplicationRunner {

  private final UserService userService;
  private final AuthService authService;

  @Override
  public void run(ApplicationArguments args) throws Exception {

    try {
      UserCreateRequest request = new UserCreateRequest("admin", "pw123", "admin@gmail.com");
      UserDto adminDto = userService.createUser(request, null);
      authService.updateRoleInner(new UserRoleUpdateRequest(adminDto.id(), Role.ADMIN));
      log.info("Admin 계정 생성됨");
    } catch (DuplicateUsernameException | DuplicateEmailException e) {
      log.warn("Admin이 이미 존재함");
    } catch (Exception e) {
      log.error("Admin 생성 중 오류");
    }
  }
}
