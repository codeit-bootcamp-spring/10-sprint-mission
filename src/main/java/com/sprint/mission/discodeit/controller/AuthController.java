package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.user.RoleUpdateRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Validated
@Tag(name = "Auth")
@Slf4j
public class AuthController {

  private final UserService userService;

  @GetMapping("/csrf-token")
  public ResponseEntity<Void> getCsrfToken(CsrfToken csrfToken) {
    String tokenValue = csrfToken.getToken();
    log.debug("CSRF 토큰 요청: {}", tokenValue);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/me")
  public ResponseEntity<UserDto> me(@AuthenticationPrincipal DiscodeitUserDetails userDetails) {
    UUID userId = userDetails.getUserDto().id();
    log.debug("인증된 유저: userId={}", userDetails.getUserDto().id());
    UserDto dto = userService.findById(userId);
    return ResponseEntity.status(HttpStatus.OK).body(dto);
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PutMapping("/role")
  public ResponseEntity<UserDto> updateUserRole(@Valid @RequestBody RoleUpdateRequest request) {
    UserDto dto = userService.updateRole(request);
    return ResponseEntity.status(HttpStatus.OK).body(dto);
  }
}
