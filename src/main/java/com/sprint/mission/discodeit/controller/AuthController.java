package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.AuthApi;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.dto.user.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController implements AuthApi {

  private final UserService userService;
  private final UserMapper userMapper;

  @Override
  @GetMapping("/csrf-token")
  public ResponseEntity<Void> getCsrfToken(CsrfToken csrfToken) {
    String tokenValue = csrfToken.getToken();
    log.debug("CSRF token requested: {}", tokenValue);

    return ResponseEntity.status(203).build(); // 203 Non-Authoritative Information 반환
  }

  @Override
  @GetMapping("/me")
  public ResponseEntity<UserDto> getMe(@AuthenticationPrincipal DiscodeitUserDetails userDetails) {
    // 세션이 없거나 만료된 상태로 접근하면 userDetails에 null 대입
    if (userDetails == null) {
      log.warn("Unauthorized access attempt to /api/auth/me");
      return ResponseEntity.status(401).build(); // 401 Unauthorized 반환
    }

    // SecurityContext에 저장되어 있던 UserDto를 꺼내서 반환
    return ResponseEntity.ok(userDetails.getUserDto());
  }

  @Override
  @PutMapping("/role")
  public ResponseEntity<UserDto> updateRole(@Valid @RequestBody UserRoleUpdateRequest request) {
    log.info("Role update requested - userId: {}, newRole: {}", request.userId(),
        request.newRole());

    User user = userService.updateUserRole(request.userId(), request.newRole());

    return ResponseEntity.ok(userMapper.toDto(user));
  }
}
