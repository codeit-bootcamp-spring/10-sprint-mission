package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.AuthDto;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.service.AutoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AutoService autoService;

    // 로그인
    @RequestMapping(method = RequestMethod.POST, path = "/login")
    public ResponseEntity<UserDto.UserResponse> login(
            @RequestBody AuthDto.LoginRequest request
    ) {
        UserDto.UserResponse userData = autoService.login(request.userName(), request.password());
        return new ResponseEntity<>(userData, HttpStatus.OK);
    }

    // 로그아웃
    @RequestMapping(method = RequestMethod.POST, path = "/logout")
    public ResponseEntity<UserDto.UserResponse> logout(
            @RequestBody AuthDto.LogoutRequest request
    ) {
        UserDto.UserResponse userData = autoService.logout(request.userName());
        return new ResponseEntity<>(userData, HttpStatus.OK);
    }
}
