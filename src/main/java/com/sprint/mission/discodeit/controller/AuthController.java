package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.LoginDto;
import com.sprint.mission.discodeit.dto.LoginResponseDto;
import com.sprint.mission.discodeit.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Auth controller 입니다.")
public class AuthController {

  private final AuthService authService;

  @RequestMapping(value = "/login", method = RequestMethod.POST)
  @Operation(summary = "로그인", operationId = "login")
  public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginDto loginDto) {
    return ResponseEntity.ok(authService.login(loginDto));
  }

}
