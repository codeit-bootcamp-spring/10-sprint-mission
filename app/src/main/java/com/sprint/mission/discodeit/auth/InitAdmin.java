package com.sprint.mission.discodeit.auth;

import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.UserDto.UserCreateRequest;
import com.sprint.mission.discodeit.dto.UserDto.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.exception.user.DuplicateEmailException;
import com.sprint.mission.discodeit.exception.user.DuplicateUsernameException;
import com.sprint.mission.discodeit.redis.RedisLockProvider;
import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.service.UserService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class InitAdmin implements ApplicationRunner {

  private final UserService userService;
  private final AuthService authService;
  private final RedisLockProvider redisLockProvider;

  @Value("${discodeit.admin.id}")
  private String adminId;
  @Value("${discodeit.admin.pw}")
  private String adminPw;
  @Value("${discodeit.admin.email}")
  private String adminEmail;

  @Override
  public void run(ApplicationArguments args) throws Exception {

    String lockKey = "lock:init:admin";

    redisLockProvider.acquireLock(lockKey);
    try {
      UserCreateRequest request = new UserCreateRequest(adminId, adminPw, adminEmail);
      UserDto adminDto = userService.createUser(request, null);

      // updateRole 하기위한 임시 어드민 권한 부여
      UsernamePasswordAuthenticationToken tempAdmin = new UsernamePasswordAuthenticationToken(
          adminDto, null, List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
      SecurityContextHolder.getContext().setAuthentication(tempAdmin);

      authService.updateRole(new UserRoleUpdateRequest(adminDto.id(), Role.ADMIN));
      log.info("Admin 계정 생성됨");
    } catch (DuplicateUsernameException | DuplicateEmailException e) {
      log.warn("Admin이 이미 존재함");
    } catch (Exception e) {
      log.error("Admin 생성 중 오류");
    } finally {
      redisLockProvider.releaseLock(lockKey);
    }
  }
}
