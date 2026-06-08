package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.authdto.RoleUpdateRequest;
import com.sprint.mission.discodeit.dto.userdto.UserDto;
import com.sprint.mission.discodeit.config.JwtTokenProvider;
import com.sprint.mission.discodeit.entity.DiscodeitUserDetails;
import com.sprint.mission.discodeit.entity.JwtInformation;
import com.sprint.mission.discodeit.entity.RefreshToken;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.enums.Role;
import com.sprint.mission.discodeit.events.RoleUpdatedEvent;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.registry.JwtRegistry;
import com.sprint.mission.discodeit.repository.RefreshTokenRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final SessionRegistry sessionRegistry;
  private final JwtTokenProvider jwtTokenProvider;
  private final RefreshTokenRepository refreshTokenRepository;
  private final JwtRegistry jwtRegistry;
  private final ApplicationEventPublisher eventPublisher;

  // 권한 수정 메서드
  // 어드민 권한만 수행
  @Transactional
  @PreAuthorize("hasRole('ADMIN')")
  public UserDto updateRole(
      RoleUpdateRequest req
  ) {
    Objects.requireNonNull(req, "유효하지 않은 요청입니다!");

    User user = userRepository.findById(req.userId())
        .orElseThrow(() -> new UserNotFoundException(req.userId()));

    Role previousRole = user.getRole();
    user.updateRole(req.newRole());
    // jwtRegistry에 기존에 존재하던 정보를 삭제
    jwtRegistry.removeJwtInformationByUserId(req.userId());
    // 권한 업데이트 시 이벤트 생성
    eventPublisher.publishEvent(new RoleUpdatedEvent(user.getId(), previousRole, req.newRole()));

    return userMapper.toDto(user);
  }

  @Transactional
  public JwtInformation refresh(String refreshToken) {
    // 올바르지 않은 토큰일 경우 예외 처리
    if (!jwtTokenProvider.validateRefreshToken(refreshToken)) {
      throw new IllegalArgumentException("Invalid refresh token");
    }

    // refreshToken으로 부터 AccessToken을 재발급
    String accessToken = jwtTokenProvider.refreshAccessToken(refreshToken);
    // 해당 Access Token으로부터 Authentication을 가져온다.
    Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
    // 해당 Authentication 객체로부터 우리가 커스텀한 UserDetail로 변환
    DiscodeitUserDetails principal = (DiscodeitUserDetails) authentication.getPrincipal();
    // 유저의 id를 가져옴
    UUID userId = principal.getUserDto().id();
    // 서버에서 저장해둔 RefreshToken을 가져옴.
    RefreshToken savedToken = refreshTokenRepository
        .findByUserId(userId)
        .orElseThrow(() -> new IllegalArgumentException("Refresh token not found"));

    // Rotation 구현
    // 요청의 refreshToken과 서버가 가지고 있는 RefreshToken이 다르면 서버 내 RefreshToken 삭제
    // 그후 예외 처리
    if (!savedToken.getToken().equals(refreshToken)) {
      refreshTokenRepository.deleteByUserId(userId);
      throw new IllegalArgumentException("Refresh token reuse detected");
    }

    // 새로운 RefreshToken을 생성하고 DB 내의 토큰을 변경
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new UserNotFoundException(userId));
    UserDto userDto = userMapper.toDto(user);
    DiscodeitUserDetails refreshedPrincipal = new DiscodeitUserDetails(userDto, "");
    Authentication refreshedAuthentication = new UsernamePasswordAuthenticationToken(
        refreshedPrincipal,
        null,
        refreshedPrincipal.getAuthorities()
    );

    accessToken = jwtTokenProvider.generateAccessToken(refreshedAuthentication);
    String newRefreshToken = jwtTokenProvider.generateRefreshToken(refreshedAuthentication);
    savedToken.updateToken(newRefreshToken);

    // refresh 후 jwtInformation을 다시 레지스트리에 담음
    JwtInformation newJwtInformation = new JwtInformation(
        userDto,
        accessToken,
        newRefreshToken
    );
    jwtRegistry.rotateJwtInformation(refreshToken, newJwtInformation);

    return newJwtInformation;
  }

  @Override
  @Transactional
  public void saveRefreshToken(UUID userId, String refreshToken) {
    RefreshToken token = refreshTokenRepository.findByUserId(userId)
        .orElseGet(() -> new RefreshToken(userId, refreshToken));
    token.updateToken(refreshToken);
    refreshTokenRepository.save(token);
  }


}
