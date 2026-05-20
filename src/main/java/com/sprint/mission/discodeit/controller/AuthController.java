package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentResponse;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.dto.user.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.UserService;
import jakarta.validation.Valid;
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
public class AuthController {

  private final UserService userService;
  private final BinaryContentService binaryContentService;
  private final BinaryContentMapper binaryContentMapper;

  public AuthController(UserService userService, BinaryContentService binaryContentService,
      BinaryContentMapper binaryContentMapper) {
    this.userService = userService;
    this.binaryContentService = binaryContentService;
    this.binaryContentMapper = binaryContentMapper;
  }

  @GetMapping("/csrf-token")
  public ResponseEntity<Void> getCsrfToken(CsrfToken csrfToken) {
    String tokenValue = csrfToken.getToken();
    log.debug("CSRF 토큰 요청: {}", tokenValue);

    return ResponseEntity.status(203).build();
  }

  @GetMapping("/me")
  public ResponseEntity<UserDto> getMe(
      @AuthenticationPrincipal DiscodeitUserDetails userDetails
  ) {
    UserDto userDto = userDetails.getUserDto();

    return ResponseEntity.ok(userDto);
  }

  @PutMapping("/role")
  public ResponseEntity<UserDto> updateRole(
      @Valid @RequestBody UserRoleUpdateRequest request
  ) {
    UserResponse updated = userService.updateRole(request);

    BinaryContentDto profile = null;
    if (updated.profileImageId() != null) {
      BinaryContentResponse profileResponse =
          binaryContentService.find(updated.profileImageId());

      profile = binaryContentMapper.toDto(profileResponse);
    }

    UserDto userDto = new UserDto(
        updated.id(),
        updated.userName(),
        updated.email(),
        profile,
        updated.online(),
        updated.role()
    );

    return ResponseEntity.ok(userDto);
  }
}