package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {
  private final UserService userService;

  @GetMapping("/csrf-token")
  public ResponseEntity<Void> csrfToken(CsrfToken csrfToken) {
    // csrfToken.getToken()을 호출하면 토큰이 실제로 생성 후 응답 Set-Cookie 헤더로 내려감
    String tokenValue = csrfToken.getToken();

    log.debug("CSRF 토큰 요청: {}", tokenValue);

    // 응답 203 , body X
    return ResponseEntity.status(HttpStatus.NON_AUTHORITATIVE_INFORMATION).build();
  }

  @GetMapping("/me")
  public ResponseEntity<UserDto> me(@AuthenticationPrincipal DiscodeitUserDetails userDetails) {
    if (userDetails == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
    UserDto currentUser = userDetails.getUserDto();

    log.debug("현재 사용자 정보 조회: userId={}, username={}", currentUser.id(), currentUser.username());

    return ResponseEntity.status(HttpStatus.OK).body(currentUser);
  }

  @PutMapping("/role")
  public ResponseEntity<UserDto> updateDto(@RequestBody @Valid UserRoleUpdateRequest request) {
    UserDto updatedUser = userService.updateRole(request);

    return ResponseEntity.status(HttpStatus.OK).body(updatedUser);
  }
}
