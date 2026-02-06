package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.user.LoginRequestDto;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@AllArgsConstructor
@RequestMapping("/login")
public class AuthController {
    private final AuthService authService;

    @RequestMapping(method = RequestMethod.POST)
    public LoginRequestDto login(
            @RequestBody LoginRequestDto dto
    ){
        authService.login(dto);
        return dto;
    }

}
