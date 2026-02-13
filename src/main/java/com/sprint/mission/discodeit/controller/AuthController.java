package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.UserLoginDto;
import com.sprint.mission.discodeit.dto.UserResponseDto;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")

public class AuthController {

  private final AuthService authService;

  // 로그인
  @RequestMapping(value = "/login", method = RequestMethod.POST)
  public UserResponseDto login(@RequestBody UserLoginDto dto) {
    return authService.login(dto);
  }
}
