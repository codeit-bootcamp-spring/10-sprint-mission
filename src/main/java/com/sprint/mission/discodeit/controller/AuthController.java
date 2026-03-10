package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@RequiredArgsConstructor
@Controller
@ResponseBody
@RequestMapping("/api/auth")
public class AuthController {

  private final AuthService authService;
  private final UserMapper userMapper;

  @RequestMapping(value="/login", method = RequestMethod.POST)
  public ResponseEntity<UserDto> login(@RequestBody LoginRequest loginRequest) {
    User user = authService.login(loginRequest);
    UserDto userDto = userMapper.toDto(user);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(userDto);
  }
}
