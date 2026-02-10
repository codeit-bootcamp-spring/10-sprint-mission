package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.AuthServiceDTO.LoginRequest;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/login")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @RequestMapping(method = RequestMethod.POST)
    public boolean login(LoginRequest request) throws IOException {
        return authService.login(request);
    }
}
