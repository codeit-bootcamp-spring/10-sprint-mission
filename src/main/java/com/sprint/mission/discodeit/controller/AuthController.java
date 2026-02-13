package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.auth.AuthLoginRequestDto;
import com.sprint.mission.discodeit.dto.auth.AuthResponseDto;
import com.sprint.mission.discodeit.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // 1. 사용자 로그인
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseEntity<AuthResponseDto> login(@RequestBody AuthLoginRequestDto loginRequestDto) {
        AuthResponseDto authResponseDto = authService.login(loginRequestDto);
        return ResponseEntity.ok(authResponseDto);
    }
}
