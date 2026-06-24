package com.sprint.mission.discodeit.service.basic;

import com.nimbusds.jwt.JWTClaimsSet;
import com.sprint.mission.discodeit.dto.auth.JwtDto;
import com.sprint.mission.discodeit.dto.auth.JwtRefreshDto;
import com.sprint.mission.discodeit.dto.auth.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.event.UserChangeEvent;
import com.sprint.mission.discodeit.event.enums.ChangeType;
import com.sprint.mission.discodeit.exception.security.InvalidJwtInformationException;
import com.sprint.mission.discodeit.exception.security.InvalidJwtTokenException;
import com.sprint.mission.discodeit.exception.security.InvalidRefreshTokenException;
import com.sprint.mission.discodeit.exception.security.JwtInformationNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.security.jwt.JwtInformation;
import com.sprint.mission.discodeit.security.jwt.JwtTokenProvider;
import com.sprint.mission.discodeit.security.registry.JwtRegistry;
import com.sprint.mission.discodeit.security.userdetails.DiscodeitUserDetails;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class BasicAuthService implements AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtRegistry jwtRegistry;

    private final ApplicationEventPublisher applicationEventPublisher;

    // 사용자 권한 수정
    @CacheEvict(value = "userList", allEntries = true)
    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public UserDto updateUserRole(UserRoleUpdateRequest request) {
        UUID userId = request.userId();
        Role newRole = request.newRole();

        log.debug("[USER_ROLE_UPDATE] 사용자 권한 수정 시작: userId={}, newRole={}",
                userId, newRole);

        User user = validateAndGetUserByUserIdWithProfile(userId);
        Role oldRole = user.getRole();

        // 기존 Role과 요청 Role이 다르면
        if (!oldRole.equals(newRole)) {
            // 권한 수정
            user.updateRole(newRole);

            // 권한 변경 시 알림 이벤트 발행
            roleUpdateEventPublish(userId, oldRole, newRole);

            // 권한이 변경된 사용자가 로그인 상태 시 강제 로그아웃 처리
            if (jwtRegistry.hasActiveJwtInformationByUserId(userId)) {
                // 해당 사용자의 모든 JwtInformation 삭제
                jwtRegistry.invalidateJwtInformationByUserId(userId);
            }

            log.debug("[USER_ROLE_UPDATE] 사용자 권한 수정 완료: userId={}, role={}",
                    user.getId(), user.getRole());

            UserDto userDto = userMapper.toDto(user);

            // 권한 변경 시 UI 랜더링을 위한 이벤트 발행
            changeEventPublish(ChangeType.UPDATED, userDto);

            return userDto;
        } else {
            log.debug("[USER_ROLE_UPDATE] 기존 권한과 요청 권한이 동일: userId={}, role={}",
                    userId, oldRole);
        }

        return userMapper.toDto(user);
    }

    // Refresh Token으로 Access Token과 새로운 Refresh Token 재발급
    @Override
    public JwtRefreshDto refreshAccessToken(String refreshToken) {
        log.debug("[REFRESH_ACCESS_TOEKN] Access Token 재발급 시작");

        // Refresh Token 검증 및 claims 반환
        JWTClaimsSet jwtClaimsSet;
        try {
            jwtClaimsSet = jwtTokenProvider.getAndValidateRefreshToken(refreshToken);
        } catch (InvalidJwtTokenException | InvalidRefreshTokenException e) {
            // 해당 API는 Refresh Token 관련 흐름으로 InvalidRefreshTokenException로 통일해서
            // SecurityExceptionHandler가 실행되게 설정
            throw new InvalidRefreshTokenException(e);
        }

        if (!jwtRegistry.hasActiveJwtInformationByRefreshToken(refreshToken)) {
            throw new InvalidRefreshTokenException();
        }

        // Refresh Token의 subject를 꺼내서 UUID로 포매팅
        UUID usrId = UUID.fromString(jwtClaimsSet.getSubject());

        User user = validateAndGetUserByUserIdWithProfile(usrId);
        UserDto userDto = userMapper.toDto(user);

        UserDto refreshUserDto = new UserDto(
                userDto.id(),
                userDto.username(),
                userDto.email(),
                userDto.profile(),
                true,
                userDto.role()
        );

        // DiscodeitUserDetails 생성
        DiscodeitUserDetails userDetails = new DiscodeitUserDetails(
                refreshUserDto,
                user.getPassword()
        );

        // 새로운 token 발급
        String newAccessToken = jwtTokenProvider.refreshAccessToken(
                jwtClaimsSet,
                userDetails
        );
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(userDetails);

        JwtDto jwtDto = new JwtDto(
                refreshUserDto,
                newAccessToken
        );

        JwtInformation newJwtInformation = new JwtInformation(
                refreshUserDto,
                newAccessToken,
                newRefreshToken
        );

        try {
            jwtRegistry.rotateJwtInformation(refreshToken, newJwtInformation);
        } catch (InvalidRefreshTokenException
                 | InvalidJwtInformationException
                 | InvalidJwtTokenException
                 | JwtInformationNotFoundException e
        ) {
            // 해당 API는 Refresh Token 관련 흐름으로 InvalidRefreshTokenException로 통일해서
            // SecurityExceptionHandler가 실행되게 설정
            throw new InvalidRefreshTokenException(e);
        }

        log.debug("[REFRESH_ACCESS_TOEKN] Access Token 재발급 완료: userId={}",
                refreshUserDto.id());

        return new JwtRefreshDto(jwtDto, newRefreshToken);
    }

    // validation
    // 사용자 존재 확인
    private User validateAndGetUserByUserIdWithProfile(UUID userId) {
        return userRepository.findByIdWithProfile(userId)
                .orElseThrow(() -> new UserNotFoundException("userId", userId));
    }

    private void roleUpdateEventPublish(UUID userId, Role oldRole, Role newRole) {
        // 권한 변경 시 알림 이벤트 발행
        applicationEventPublisher.publishEvent(
                new RoleUpdatedEvent(userId, oldRole, newRole)
        );
    }

    private void changeEventPublish(ChangeType changeType, UserDto userDto) {
        applicationEventPublisher.publishEvent(
                new UserChangeEvent(
                        changeType,
                        userDto
                )
        );
    }
}
