package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.exception.ErrorResponse;
import com.sprint.mission.discodeit.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Auth")
@RequestMapping("/api/auth")
public class AuthController {

  private final AuthService authService;

  // 로그인
  @Operation(summary = "로그인")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "로그인 성공"),
      @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  @PostMapping(value = "/login")
  public ResponseEntity<UserDto> login(@Valid @RequestBody UserDto.UserLoginRequest loginReq) {
    log.info("[Controller] 로그인 요청: username={}", loginReq.username());

    UserDto dto = authService.login(loginReq);
    log.debug("[Controller] 로그인 응답 준비: id={}, username={}", dto.id(), dto.username());

    return ResponseEntity.status(HttpStatus.OK).body(dto);
  }
}
