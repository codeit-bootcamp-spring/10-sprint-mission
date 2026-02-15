package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.auth.AuthLoginRequestDTO;
import com.sprint.mission.discodeit.dto.response.UserDto;
import com.sprint.mission.discodeit.entity.UserEntity;
import com.sprint.mission.discodeit.entity.UserStatusEntity;
import com.sprint.mission.discodeit.entity.UserStatusType;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {
    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;

    // 로그인
    public UserDto login(AuthLoginRequestDTO authLoginRequestDTO) {
        UserEntity targetUser = userRepository.findById(authLoginRequestDTO.userId())
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 존재하지 않습니다."));
        UserStatusEntity targetUserStatus = userStatusRepository.findByUserId(targetUser.getId());

        if (!targetUser.getPassword().equals(authLoginRequestDTO.password()) ||
                !targetUser.getUsername().equals(authLoginRequestDTO.username())) {
            throw new IllegalArgumentException(("[로그인 실패] 잘못된 닉네임 혹은 비밀번호를 입력하셨습니다."));
        }

        return UserDto.builder()
                .id(targetUser.getId())
                .username(targetUser.getUsername())
                .email(targetUser.getEmail())
                .online(targetUserStatus.getStatus() == UserStatusType.ONLINE)
                .profileId(targetUser.getProfileId())
                .createdAt(targetUser.getCreatedAt())
                .updatedAt(targetUser.getUpdatedAt())
                .build();
    }
}
