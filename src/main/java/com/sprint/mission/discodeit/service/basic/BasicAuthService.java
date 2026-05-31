package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.jwt.JwtRegistry;
import com.sprint.mission.discodeit.jwt.JwtTokenProvider;
import com.sprint.mission.discodeit.jwt.TokenRefreshResult;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class BasicAuthService implements AuthService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final JwtTokenProvider jwtTokenProvider;
  private final JwtRegistry jwtRegistry;

  @Transactional
  @Override
  public UserDto updateRole(UserRoleUpdateRequest request) {
    log.debug("사용자 역할 변경 시작: userId={}, newRole={}", request.userId(), request.role());

    User user = userRepository.findById(request.userId())
        .orElseThrow(() -> UserNotFoundException.withId(request.userId()));

    user.updateRole(request.role());
    userRepository.save(user);

    jwtRegistry.invalidateJwtInformationByUserId(request.userId());

    log.info("사용자 역할 변경 완료: userId={}, newRole={}", request.userId(), request.role());
    return userMapper.toDto(user);
  }

  @Override
  public TokenRefreshResult refresh(String refreshToken) {
    if (refreshToken == null
        || !jwtTokenProvider.validateRefreshToken(refreshToken)
        || !jwtRegistry.hasActiveJwtInformationByRefreshToken(refreshToken)) {
      throw new DiscodeitException(ErrorCode.INVALID_REFRESH_TOKEN);
    }

    UUID userId = jwtTokenProvider.getUserId(refreshToken);
    String username = jwtTokenProvider.getUsername(refreshToken);
    String role = jwtTokenProvider.getRole(refreshToken);

    String newAccessToken = jwtTokenProvider.generateAccessToken(userId, username, role);
    String newRefreshToken = jwtTokenProvider.generateRefreshToken(userId, username, role);

    jwtRegistry.rotateJwtInformation(newAccessToken, newRefreshToken);

    log.info("토큰 재발급: userId={}, username={}", userId, username);
    return new TokenRefreshResult(newAccessToken, newRefreshToken);
  }
}
