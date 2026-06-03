package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.auth.RoleUpdateRequest;
import com.sprint.mission.discodeit.dto.response.auth.JwtDto;
import com.sprint.mission.discodeit.dto.response.UserDto;
import com.sprint.mission.discodeit.dto.response.auth.TokenDto;
import com.sprint.mission.discodeit.entity.UserEntity;
import com.sprint.mission.discodeit.exception.auth.JwtTokenUnauthorizedException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.AuthMapper;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.security.jwt.provider.JwtTokenProvider;
import com.sprint.mission.discodeit.security.auth.DiscodeitUserDetails;
import com.sprint.mission.discodeit.security.jwt.registry.JwtRegistry;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BasicAuthService implements AuthService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;
    private final AuthMapper authMapper;

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtRegistry jwtRegistry;

    private final UserDetailsService userDetailsService;

    // refreshToken 재발급
    @Override
    public TokenDto reissueRefreshToken(String refreshToken) {
        // JWT 토큰 검증
        if (!StringUtils.hasText(refreshToken) || !jwtTokenProvider.validateToken(refreshToken)) {
            throw new JwtTokenUnauthorizedException();
        }

        // 토큰을 재발급 받는 사용자 정보 조회
        Authentication authentication = jwtTokenProvider.getAuthentication(refreshToken);
        String userIdStr = jwtTokenProvider.getUserId(refreshToken);
        DiscodeitUserDetails userDetails =
                (DiscodeitUserDetails) ((DiscodeitUserDetailsService) userDetailsService).loadUserById(userIdStr);

        // 토큰 재발급
        Authentication newAuthentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        String newAccessToken = jwtTokenProvider.generateAccessToken(newAuthentication);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(newAuthentication);

        JwtDto jwtDto = authMapper.toJwtDto(newAccessToken, userDetails.getUserDto());

        return authMapper.toTokenDto(jwtDto, newRefreshToken);
    }

    // 사용자 권한 변경
    @Override
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public UserDto updateUserRole(RoleUpdateRequest roleUpdateRequest) {
        UserEntity targetUser = getUserEntityOrThrow(roleUpdateRequest.userId());

        targetUser.updateRole(roleUpdateRequest.newRole());

        // 권한이 변경된, 특정 사용자 강제 로그아웃
        jwtRegistry.invalidateJwtInformationByUserId(targetUser.getId());

        return userMapper.toDto(targetUser, false);
    }

    // 사용자 반환 (userId)
    private UserEntity getUserEntityOrThrow(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }
}