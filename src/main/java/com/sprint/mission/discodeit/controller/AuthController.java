package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.AuthLoginRequestDTO;
import com.sprint.mission.discodeit.dto.response.AuthResponseDTO;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<AuthResponseDTO> login(@RequestBody AuthLoginRequestDTO authLoginRequestDTO) {
        AuthResponseDTO response = authService.login(authLoginRequestDTO);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
}
