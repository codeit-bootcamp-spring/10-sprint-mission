package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.auth.RoleUpdateRequest;
import com.sprint.mission.discodeit.dto.response.UserDto;
import com.sprint.mission.discodeit.dto.response.auth.TokenDto;

public interface AuthService {
    // refreshToken 재발급
    TokenDto reissueRefreshToken(String refreshToken);

    // 사용자 권한 변경
    UserDto updateUserRole(RoleUpdateRequest roleUpdateRequest);
}
