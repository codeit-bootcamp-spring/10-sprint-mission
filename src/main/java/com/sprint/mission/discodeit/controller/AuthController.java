package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.LoginRequest;
import com.sprint.mission.discodeit.dto.UserResponse;
import com.sprint.mission.discodeit.service.basic.BasicAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final BasicAuthService authService;

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public UserResponse login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }
}