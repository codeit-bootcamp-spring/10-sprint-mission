package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.LoginDto;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @RequestMapping(method = RequestMethod.POST, value = "/login")
    public ResponseEntity<UserDto.Response> login(@RequestBody LoginDto.LoginRequest request) {
        UserDto.Response response = authService.login(request);
        // todo: 여기다가 로그인 유지를 위한 세션을 생성해야 할 듯
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}

