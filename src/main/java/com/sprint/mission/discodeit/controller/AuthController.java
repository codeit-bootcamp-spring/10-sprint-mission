package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.auth.AuthLoginRequestDTO;
import com.sprint.mission.discodeit.dto.response.UserResponseDTO;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    // 로그인
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseEntity<UserResponseDTO> login(@RequestBody AuthLoginRequestDTO authLoginRequestDTO) {
        UserResponseDTO response = authService.login(authLoginRequestDTO);

        return ResponseEntity.ok(response);
    }
}
