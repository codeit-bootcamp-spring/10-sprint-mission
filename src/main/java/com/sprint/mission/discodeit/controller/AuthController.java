package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/login")
public class AuthController {

    private final AuthService authService;

    //사용자는 로그인할 수 있다.
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<?> login (@ModelAttribute UserDto.Login request) {
        UserDto.Response response = authService.login(request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
