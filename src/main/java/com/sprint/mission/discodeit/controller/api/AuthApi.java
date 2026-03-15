package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.LoginRequest;
import org.springframework.http.ResponseEntity;

public interface AuthApi {

    ResponseEntity<UserDto> login(LoginRequest loginRequest);
}