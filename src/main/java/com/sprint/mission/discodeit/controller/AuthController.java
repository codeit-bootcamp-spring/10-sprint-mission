package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.auth.LoginRequest;
import com.sprint.mission.discodeit.dto.response.UserDto;
import com.sprint.mission.discodeit.exception.ErrorDto;
import com.sprint.mission.discodeit.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {

  private final AuthService authService;

  @Operation(summary = "로그인")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200", description = "로그인 성공",
          content = @Content(schema = @Schema(implementation = UserDto.class))
      ),
      @ApiResponse(
          responseCode = "400", description = "요청 검증 실패",
          content = @Content(
              schema = @Schema(implementation = ErrorDto.class),
              examples = @ExampleObject(value =
                  "{ \"status\": 400, \"error\": \"VALIDATION_ERROR\", "
                      + "\"message\": \"비밀번호를 입력해주세요.\", \"time\": \"2026-02-23T05:23:49.657764500Z\" }")
          )
      ),
      @ApiResponse(
          responseCode = "400", description = "비밀번호가 일치하지 않음",
          content = @Content(
              schema = @Schema(implementation = ErrorDto.class),
              examples = @ExampleObject(value =
                  "{ \"status\": 400, \"error\": \"INVALID_CREDENTIALS\", "
                      + "\"message\": \"유저 이름 또는 비밀번호가 올바르지 않습니다.\", \"time\": \"2026-02-23T05:23:49.657764500Z\" }")
          )
      ),
      @ApiResponse(
          responseCode = "404", description = "사용자를 찾을 수 없음",
          content = @Content(
              schema = @Schema(implementation = ErrorDto.class),
              examples = @ExampleObject(value = "{ \"status\": 404, \"error\": \"USER_NOT_FOUND\", "
                  + "\"message\": \"유저를 찾을 수 없습니다.\", \"time\": \"2026-02-23T05:23:49.657764500Z\" }")
          )
      )
  })
  @PostMapping(path = "login")
  public ResponseEntity<UserDto> login(
      @Parameter(description = "로그인 정보") @Valid @RequestBody LoginRequest loginRequest) {
    UserDto user = authService.login(loginRequest);
    return ResponseEntity.status(HttpStatus.OK).body(user);
  }
}
