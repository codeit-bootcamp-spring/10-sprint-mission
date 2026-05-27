package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

  private final UserService userService;

  @GetMapping(path = "csrf-token")
  public ResponseEntity<Void> getCsrfToken(CsrfToken csrfToken) {
    String tokenValue = csrfToken.getToken();
    log.debug("CSRF 토큰 요청: {}", tokenValue);
    return ResponseEntity
            .status(HttpStatus.NON_AUTHORITATIVE_INFORMATION)
            .build();
  }

  @GetMapping(path = "me")
  public ResponseEntity<UserDto> getCurrentUser(
          @AuthenticationPrincipal DiscodeitUserDetails userDetails
  ) {
    UserDto userDto = userDetails.getUserDto();
    return ResponseEntity
            .status(HttpStatus.OK)
            .body(userDto);
  }

  @PutMapping(path = "role")
  public ResponseEntity<UserDto> updateRole(
          @RequestBody @Valid UserRoleUpdateRequest request
  ) {
    UserDto updatedUser = userService.updateRole(request);
    return ResponseEntity
            .status(HttpStatus.OK)
            .body(updatedUser);
  }
}
