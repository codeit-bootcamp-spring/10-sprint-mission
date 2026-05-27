package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.auth.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.security.session.UserSessionManager;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    private final UserSessionManager userSessionManager;

    // 사용자 권한 수정
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

            // 권한이 변경된 사용자의 로그인 세션 만료 처리
            userSessionManager.expiredUserSession(userId);

            log.debug("[USER_ROLE_UPDATE] 사용자 권한 수정 완료: userId={}, role={}",
                    user.getId(), user.getRole());
        } else {
            log.debug("[USER_ROLE_UPDATE] 기존 권한과 요청 권한이 동일: userId={}, role={}",
                    userId, oldRole);
        }

        return userMapper.toDto(user);
    }

    // validation
    // 사용자 존재 확인
    private User validateAndGetUserByUserIdWithProfile(UUID userId) {
        return userRepository.findByIdWithProfile(userId)
                .orElseThrow(() -> new UserNotFoundException("userId", userId));
    }
}
