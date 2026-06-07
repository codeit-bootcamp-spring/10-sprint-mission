package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.auth.DiscodeitUserDetails;
import com.sprint.mission.discodeit.auth.dto.JwtInformation;
import com.sprint.mission.discodeit.auth.jwt.JwtRegistry;
import com.sprint.mission.discodeit.auth.jwt.JwtTokenProvider;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.UserDto.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.auth.InvalidTokenException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BasicAuthService implements AuthService {

  private final UserDetailsService userDetailsService;
  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final JwtRegistry jwtRegistry;
  private final JwtTokenProvider jwtTokenProvider;

  @PreAuthorize("hasRole('ADMIN')")
  @Transactional
  public UserDto updateRole(UserRoleUpdateRequest request) {
    return updateRoleInner(request);
  }

  @Transactional
  public UserDto updateRoleInner(UserRoleUpdateRequest request) {
    log.debug("유저 role 변경 요청 - userId={}, newRole={}", request.userId(), request.newRole());

    User findUser = userRepository.findById(request.userId())
        .orElseThrow(() -> new UserNotFoundException());

    findUser.updateRole(request.newRole());
    UserDto userDto = userMapper.toDto(findUser);

    // 세션만료
    jwtRegistry.invalidateJwtInformationByUserId(userDto.id());
    log.debug("유저 세션 삭제 완료 - userId={}", request.userId());

    log.info("유저 role 변경 성공 - userId={}, newRole={}", request.userId(), request.newRole());
    return userDto;
  }

  @Transactional
  public JwtInformation updateRefreshToken(String previousRefreshToken) {
    log.debug("refresh token 갱신 요청");

    if (!jwtTokenProvider.validateToken(previousRefreshToken) ||
        !jwtRegistry.hasActiveJwtInformationByRefreshToken(previousRefreshToken)) {
      throw new InvalidTokenException();
    }

    Map<String, Object> claims = jwtTokenProvider.getClaims(previousRefreshToken);

    // user
    String username = getUsernameFromClaims(claims);
    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
    User findUser = userRepository.findByUsername(username)
        .orElseThrow(() -> new UserNotFoundException());

    DiscodeitUserDetails discodeitUserDetails = (DiscodeitUserDetails) userDetails;
    String accessToken = jwtTokenProvider.generateAccessToken(discodeitUserDetails);
    String refreshToken = jwtTokenProvider.generateRefreshToken(discodeitUserDetails);
    JwtInformation newJwtInformation = new JwtInformation(userMapper.toDto(findUser), accessToken,
        refreshToken);

    jwtRegistry.rotateJwtInformation(previousRefreshToken, newJwtInformation);

    log.info("refresh token 갱신 성공");
    return newJwtInformation;
  }

  private String getUsernameFromClaims(Map<String, Object> claims) {
    return claims.get("sub").toString();
  }
}
