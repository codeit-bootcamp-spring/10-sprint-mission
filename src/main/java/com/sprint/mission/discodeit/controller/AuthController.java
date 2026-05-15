package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.authdto.RoleUpdateRequest;
import com.sprint.mission.discodeit.dto.userdto.UserDto;
import com.sprint.mission.discodeit.entity.DiscodeitUserDetails;
import com.sprint.mission.discodeit.service.basic.AuthService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Controller
public class AuthController {

  private final AuthService authService;

  @GetMapping("/me")
  public ResponseEntity<UserDto> getUserViaSession(
      @AuthenticationPrincipal DiscodeitUserDetails userDetails
  ) {
    return ResponseEntity.ok(userDetails.getUserDto());
  }

  @PutMapping("/role")
  public ResponseEntity<UserDto> roleUpdate(
      @RequestBody RoleUpdateRequest req
  ) {
    return ResponseEntity.ok(authService.updateRole(req));

  }
}
