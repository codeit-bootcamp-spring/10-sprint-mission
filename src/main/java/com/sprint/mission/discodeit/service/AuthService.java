package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.auth.RoleUpdateRequest;
import com.sprint.mission.discodeit.dto.user.response.UserDTO;

public interface AuthService {
    UserDTO updateRole(RoleUpdateRequest request);
}
