package com.sprint.mission.discodeit.auth.controller;

import com.sprint.mission.discodeit.auth.service.AuthService;
import com.sprint.mission.discodeit.auth.dto.UserLoginRequest;
import com.sprint.mission.discodeit.user.dto.UserDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.media.ExampleObject;

@Tag(name = "Auth")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;

  @Operation(summary = "로그인")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "로그인 성공"
      ),
      @ApiResponse(
          responseCode = "400",
          description = "비밀번호가 일치하지 않음",
          content = @Content(
              examples = @ExampleObject(value = "Wrong password")
          )
      ),
      @ApiResponse(
          responseCode = "404",
          description = "사용자를 찾을 수 없음",
          content = @Content(
              examples = @ExampleObject(value = "User with username {username} not found")
          )
      )
  })
  @PostMapping("/login")
  public ResponseEntity<UserDto> login(
      @io.swagger.v3.oas.annotations.parameters.RequestBody
      @Valid @RequestBody UserLoginRequest request) {
    UserDto user = authService.login(request);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(user);
  }
}