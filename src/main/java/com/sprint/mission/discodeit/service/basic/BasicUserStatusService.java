package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.userstatusdto.UserStatusDto;
import com.sprint.mission.discodeit.dto.userstatusdto.UserStatusUpdateRequestDTO;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.UserStatusDTOMapper;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.UUID;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BasicUserStatusService implements UserStatusService {

    private final UserStatusRepository userStatusRepository;
    private final UserStatusDTOMapper userStatusDTOMapper;

    @Override
    public UserStatus find(UUID id) {
        Objects.requireNonNull(id, "유효하지 않은 ID 입니다!");
        return userStatusRepository.findById(id)
            .orElseThrow(
                () -> new NoSuchElementException("해당 UserStatus를 찾을 수 없습니다!"));
    }

    @Override
    @Transactional
    public UserStatusDto activateUserOnline(UUID userId, UserStatusUpdateRequestDTO req) {
        Objects.requireNonNull(userId, "유효하지 않은 사용자 ID!");
        Objects.requireNonNull(req, "유효하지 않은 생성 요청!");
        Optional<UserStatus> optUserStatus = userStatusRepository.findByUserId(userId);
        if (optUserStatus.isEmpty()) {
            throw new NoSuchElementException("해당 UserStatus 찾을 수 없습니다!");
        }
        UserStatus userStatus = optUserStatus.get();
        userStatus.update(req.newLastActiveAt());

        return userStatusDTOMapper.toDto(userStatus);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        Objects.requireNonNull(id, "유효하지 않은 ID 입니다!");
        userStatusRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("해당 UserStatus를 찾을 수 없습니다!"));
        userStatusRepository.deleteById(id);
    }

}
