package com.sprint.mission.discodeit.user.controller;

import com.sprint.mission.discodeit.user.dto.UserInfo;
import com.sprint.mission.discodeit.user.dto.UserLoginInfo;
import com.sprint.mission.discodeit.user.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    @RequestMapping(value = "/login", method = RequestMethod.POST, consumes = "application/json")
    public ResponseEntity<UserInfo> login(@RequestBody UserLoginInfo loginInfo) {
        return ResponseEntity.ok(authService.login(loginInfo));
    }
}
