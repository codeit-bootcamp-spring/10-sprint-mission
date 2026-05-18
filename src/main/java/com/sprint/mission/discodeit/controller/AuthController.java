package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.auth.DiscodeitUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "1. 사용자 및 인증 그룹", description = "인증 및 권한 관리 API")
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

  private final UserService userService;

  @Operation(summary = "CSRF 토큰 발급", description = "SPA 환경에서 사용하기 위한 CSRF 토큰을 쿠키에 발급합니다.")
  @GetMapping("csrf-token")
  public ResponseEntity<Void> getCsrfToken(CsrfToken csrfToken) {
    String tokenValue = csrfToken.getToken();
    log.debug("CSRF 토큰 요청: {}", tokenValue);

    return ResponseEntity.status(HttpStatus.NON_AUTHORITATIVE_INFORMATION).build();
  }

  @Operation(summary = "현재 사용자 정보 조회", description = "세션 ID를 통해 인증된 사용자의 정보를 조회합니다.")
  @GetMapping("me")
  public ResponseEntity<UserDto> getCurrentUser(
      @AuthenticationPrincipal DiscodeitUserDetails discodeitUserDetails
  ) {
    UserDto currentUser = discodeitUserDetails.getUserDto();
    log.debug("현재 사용자 조회 요청: {}", currentUser.getEmail());

    return ResponseEntity.ok(currentUser);
  }

  @Operation(summary = "사용자 권한 수정", description = "특정 사용자의 권한을 수정합니다.")
  @PutMapping("role")
  public ResponseEntity<UserDto> updateUserRole(
      @RequestBody UserRoleUpdateRequest userRoleUpdateRequest
  ) {
    UserDto updatedUser = userService.updateUserRole(userRoleUpdateRequest);
    log.debug("사용자 권한 수정 요청 = ID: {}, NewRole: {}", userRoleUpdateRequest.getUserId(),
        userRoleUpdateRequest.getNewRole());

    return ResponseEntity.ok(updatedUser);
  }

}
