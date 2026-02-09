package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AuthController {
    private final AuthService authService;

    // 로그인
    @RequestMapping(value = "login", method = RequestMethod.POST)
    public ResponseEntity<UserDto.response> login(@RequestBody UserDto.LoginRequest loginReq) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(authService.login(loginReq));
    }
}
