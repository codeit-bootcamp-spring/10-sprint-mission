package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.jwt.JwtInformation;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.DiscodeitUnauthorizedException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.registry.JwtRegistry;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.security.JwtTokenProvider;
import com.sprint.mission.discodeit.service.AuthService;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class BasicAuthService implements AuthService {

  private final JwtRegistry jwtRegistry;
  private final JwtTokenProvider jwtTokenProvider;
  private final UserRepository userRepository;
  private final UserMapper userMapper;

  @Override
  public void expireUserSessions(UUID userId) {
    jwtRegistry.invalidateJwtInformationByUserId(userId);
    log.debug("[AUTH] 권한 변경 유저 로그인 상태 변경: userId={}", userId);
  }

  @Override
  public boolean isUserLoggedIn(UUID userId) {
    return jwtRegistry.hasActiveJwtInformationByUserId(userId);
  }

  @Override
  public JwtInformation rotateToken(String oldRefreshToken) {
    UUID userId = UUID.fromString(jwtTokenProvider.getSubject(oldRefreshToken));
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new UserNotFoundException(Map.of("userId", userId)));
    UserDto userDto = userMapper.toDto(user, false);
    String newAccessToken = delegateAccessToken(userDto);
    String newRefreshToken = delegateRefreshToken(userDto);
    JwtInformation newJwtInformation = new JwtInformation(
        userDto,
        newAccessToken,
        newRefreshToken
    );
    jwtRegistry.rotateJwtInformation(oldRefreshToken, newJwtInformation);
    return newJwtInformation;
  }

  private String delegateAccessToken(UserDto userDto) {
    Map<String, Object> claims = new HashMap<>();
    claims.put("username", userDto.username());
    claims.put("email", userDto.email());
    claims.put("roles", userDto.role().getDbKey());
    claims.put("userId", userDto.id());

    String subject = userDto.id().toString();

    return jwtTokenProvider.generateAccessToken(
        claims, subject);
  }

  private String delegateRefreshToken(UserDto userDto) {
    String subject = userDto.id().toString();
    return jwtTokenProvider.generateRefreshToken(subject);
  }
}
