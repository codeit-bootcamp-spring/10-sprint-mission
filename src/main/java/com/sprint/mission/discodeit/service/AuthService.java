package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.JwtDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.dto.request.RoleUpdateRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {

//  UserDto login(LoginRequest loginRequest);
    UserDto updateUserRole(RoleUpdateRequest request);

    JwtDto refresh(String refreshToken, HttpServletResponse response);
}
