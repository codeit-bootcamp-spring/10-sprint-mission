package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.auth.jwt.JwtTokenProvider;
import com.sprint.mission.discodeit.dto.data.JwtDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.dto.request.RoleUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.details.DiscodeitUserDetails;
import com.sprint.mission.discodeit.exception.user.InvalidCredentialsException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class BasicAuthService implements AuthService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final SessionRegistry sessionRegistry;
  private final UserService userService;
  private final JwtTokenProvider jwtTokenProvider;
  private final UserDetailsService userDetailsService;

  /*
  @Transactional(readOnly = true)
  @Override
  public UserDto login(LoginRequest loginRequest) {
    log.debug("로그인 시도: username={}", loginRequest.username());

    String username = loginRequest.username();
    String password = loginRequest.password();

    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> UserNotFoundException.withUsername(username));

    if (!user.getPassword().equals(password)) {
      throw InvalidCredentialsException.wrongPassword();
    }

    log.info("로그인 성공: userId={}, username={}", user.getId(), username);
    return userMapper.toDto(user);
  }
   */

  // Role 변경
  @Transactional
  @PreAuthorize( "hasRole('ADMIN')")
  public UserDto updateUserRole(RoleUpdateRequest request) {
    log.debug("사용자 Role 변경 시작");

    User user = userRepository.findById(request.userId())
            .orElseThrow(() -> UserNotFoundException.withId(request.userId()));

    user.updateRole(request.newRole());
    userRepository.save(user);

    expiredUserSession(user.getId());

    log.debug("사용자 Role 변경 완료");

    return userMapper.toDto(user, userService.isLoggedIn(user.getId()));
  }

  public JwtDto refresh(String refreshToken, HttpServletResponse response) {
    if (refreshToken == null || refreshToken.isBlank()) {
      throw new RuntimeException("Refresh token is null or blank");
    }

    Map<String, Object> refreshClaims = jwtTokenProvider.getClaims(refreshToken);
    String email = String.valueOf(refreshClaims.get("sub"));

    DiscodeitUserDetails userDetails = (DiscodeitUserDetails) userDetailsService.loadUserByUsername(email);
    UserDto userDto = userDetails.getUserDto();

    List<String> roles = userDetails.getAuthorities().stream()
            .map(GrantedAuthority::toString)
            .toList();

    Map<String, Object> accessClaims = Map.of(
            "roles", roles
    );

    String newAccessToken = jwtTokenProvider.generateAccessToken(accessClaims, email);
    String newRefreshToken = jwtTokenProvider.generateRefreshToken(email);

    Cookie refreshTokenCookie = new Cookie("REFRESH_TOKEN", newRefreshToken);

    refreshTokenCookie.setHttpOnly(true);
    refreshTokenCookie.setSecure(true);
    refreshTokenCookie.setPath("/");
    refreshTokenCookie.setMaxAge(60 * 60 * 24 * 7);

    response.addCookie(refreshTokenCookie);
    return new JwtDto(userDto, newAccessToken);
  }

  private void expiredUserSession(UUID userId) {
    sessionRegistry.getAllPrincipals().stream()
            .filter(principal -> principal instanceof DiscodeitUserDetails)
            .map(principal -> (DiscodeitUserDetails) principal)
            .filter(userDetails -> userDetails.getUserDto().id().equals(userId))
            .forEach(userDetails -> sessionRegistry.getAllSessions(userDetails, false)
                    .forEach(SessionInformation::expireNow)
            );
  }
}
