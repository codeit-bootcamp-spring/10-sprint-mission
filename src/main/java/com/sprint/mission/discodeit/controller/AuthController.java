package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.auth.request.AuthServiceRequest;
import com.sprint.mission.discodeit.dto.auth.response.AuthServiceResponse;
import com.sprint.mission.discodeit.service.auth.AuthService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    //로그인 기능
    @RequestMapping(value="/login", method= RequestMethod.POST)
    AuthServiceResponse login(@RequestBody AuthServiceRequest request){
        return authService.login(request);
    }
}
