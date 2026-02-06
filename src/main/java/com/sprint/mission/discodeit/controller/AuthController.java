package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.user.LoginRequestDto;
import com.sprint.mission.discodeit.dto.user.LoginResponseDto;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/login")
public class AuthController {
    private final AuthService authService;

    @RequestMapping(method = RequestMethod.POST)
    public LoginResponseDto login(
            @RequestBody LoginRequestDto dto
    ){
        return authService.login(dto);
    }

}
