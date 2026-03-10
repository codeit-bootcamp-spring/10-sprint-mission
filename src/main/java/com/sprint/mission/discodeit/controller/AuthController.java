package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.auth.LoginRequest;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentResponse;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

  private final AuthService authService;
  private final UserService userService;
  private final BinaryContentService binaryContentService;

  public AuthController(AuthService authService, UserService userService,
      BinaryContentService binaryContentService) {
    this.authService = authService;
    this.userService = userService;
    this.binaryContentService = binaryContentService;
  }

  @Operation(summary = "로그인", operationId = "login", tags = {"Auth"})
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "로그인 성공"),
      @ApiResponse(responseCode = "400", description = "비밀번호가 일치하지 않음"),
      @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
  })
  @RequestMapping(value = "/login", method = RequestMethod.POST)
  public ResponseEntity<UserDto> postLogin(@RequestBody LoginRequest dto) {
    var user = authService.login(dto);
    UserResponse userResponse = userService.find(user.getId());

    return ResponseEntity.ok(toUserDto(userResponse));
  }

  private UserDto toUserDto(UserResponse userResponse) {
    BinaryContentDto profile = null;
    if (userResponse.profileImageId() != null) {
      BinaryContentResponse binary = binaryContentService.find(userResponse.profileImageId());
      profile = new BinaryContentDto(
          binary.id(),
          binary.fileName(),
          binary.size(),
          binary.contentType()
      );
    }

    return new UserDto(
        userResponse.id(),
        userResponse.userName(),
        userResponse.email(),
        profile,
        userResponse.online()
    );
  }
}