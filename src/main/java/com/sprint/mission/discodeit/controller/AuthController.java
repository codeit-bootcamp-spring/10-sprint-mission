package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth", description = "인증 API")
public class AuthController {

  private final AuthService authService;

  @RequestMapping(path = "/login", method = RequestMethod.POST)
  @Operation(summary = "로그인")
  @ApiResponses({
          @ApiResponse(responseCode = "200", description = "로그인 성공",
                  content = @Content(mediaType = "application.json",
                          schema = @Schema(implementation = UserDto.class)
                  )
          ),
          @ApiResponse(responseCode = "400", description = "비밀번호가 일치하지 않음",
                  content = @Content(
                          examples = @ExampleObject(value = "Wrong password")
                  )
          ),
          @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음",
                  content = @Content(
                          examples = @ExampleObject(value = "User with username {username} not found")
                  )
          )
  })
  public ResponseEntity<User> login(@RequestBody LoginRequest loginRequest) {
    User user = authService.login(loginRequest);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(user);
  }
}
