package com.sprint.mission.discodeit.auth.controller;

import com.sprint.mission.discodeit.auth.service.AuthService;
import com.sprint.mission.discodeit.jwt.JwtDto;
import com.sprint.mission.discodeit.user.dto.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.user.dto.UserDto;
import com.sprint.mission.discodeit.user.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Auth")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

  private final UserService userService;
  private final AuthService authService;

  @GetMapping("/csrf-token")
  public ResponseEntity<Void> getCsrfToken(CsrfToken csrfToken) {
    String tokenValue = csrfToken.getToken();
    log.debug("CSRF 토큰 요청: {}", tokenValue);
    return ResponseEntity.status(203).build();
  }

  @PutMapping("/role")
  public ResponseEntity<UserDto> updateRole(
      @RequestBody UserRoleUpdateRequest request) {
    UserDto updatedUser = userService.updateRole(request);
    return ResponseEntity.ok(updatedUser);
  }

  @PostMapping("/refresh")
  public ResponseEntity<JwtDto> refreshToken(
      @CookieValue(value = "REFRESH_TOKEN", required = false) String refreshToken,
      HttpServletResponse response) {
    return ResponseEntity.ok(authService.refresh(refreshToken, response));
  }
}