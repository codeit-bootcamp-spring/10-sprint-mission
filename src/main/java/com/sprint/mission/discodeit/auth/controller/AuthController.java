package com.sprint.mission.discodeit.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.auth.DiscodeitUserDetails;
import com.sprint.mission.discodeit.auth.service.AuthService;
import com.sprint.mission.discodeit.jwt.JwtDto;
import com.sprint.mission.discodeit.jwt.JwtTokenProvider;
import com.sprint.mission.discodeit.user.dto.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.user.dto.UserDto;
import com.sprint.mission.discodeit.user.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
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
  private final JwtTokenProvider jwtTokenProvider;
  private final UserDetailsService userDetailsService;

  @GetMapping("/csrf-token")
  public ResponseEntity<Void> getCsrfToken(CsrfToken csrfToken) {
    String tokenValue = csrfToken.getToken();
    log.debug("CSRF 토큰 요청: {}", tokenValue);
    return ResponseEntity.status(203).build();
  }

//  @GetMapping("/me")
//  public ResponseEntity<UserDto> me(@AuthenticationPrincipal DiscodeitUserDetails userDetails) {
//    return ResponseEntity.ok(userDetails.getUserDto());
//  }

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