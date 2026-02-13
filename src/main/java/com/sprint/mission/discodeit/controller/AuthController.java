package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.user.UserLoginDto;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.service.AuthService;
import jakarta.validation.Valid;
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

    @RequestMapping(path = "/login", method = RequestMethod.POST)
    public ResponseEntity<UserDto> login (@RequestBody @Valid UserLoginDto dto) {
        return ResponseEntity.status(HttpStatus.OK).body(authService.login(dto));
    }
}
