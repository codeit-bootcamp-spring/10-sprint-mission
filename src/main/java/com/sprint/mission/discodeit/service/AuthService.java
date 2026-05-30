package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.authdto.RoleUpdateRequest;
import com.sprint.mission.discodeit.dto.jwtdto.JwtInformation;
import com.sprint.mission.discodeit.dto.userdto.UserDto;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public interface AuthService {

  public UserDto updateRole(RoleUpdateRequest req);

  JwtInformation refresh(String refreshToken);

  void saveRefreshToken(UUID userId, String refreshToken);
}
