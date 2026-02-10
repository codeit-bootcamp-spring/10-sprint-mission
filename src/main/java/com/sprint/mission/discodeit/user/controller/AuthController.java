package com.sprint.mission.discodeit.user.controller;

import com.sprint.mission.discodeit.user.dto.UserLoginRequest;
import com.sprint.mission.discodeit.user.dto.UserResponse;
import com.sprint.mission.discodeit.user.entity.User;
import com.sprint.mission.discodeit.user.mapper.UserMapper;
import com.sprint.mission.discodeit.user.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final UserMapper userMapper;

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public UserResponse login(@RequestBody UserLoginRequest request) {
        User user = authService.login(request);
        return userMapper.convertToResponse(user);
    }
}