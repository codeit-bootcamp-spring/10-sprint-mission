package com.sprint.mission.discodeit.service.basic;

//import com.sprint.mission.discodeit.dto.userstatusdto.UserStatusRequestDTO;

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

    @Override
    public UserStatus find(UUID id) {
        Objects.requireNonNull(id, "유효하지 않은 ID입니다!");
        return userStatusRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("해당 UserStatus는 존재하지 않습니다!"));
    }

    @Override
    @Transactional
    public UserStatusDto activateUserOnline(UUID userId, UserStatusUpdateRequestDTO req) {
        Objects.requireNonNull(userId, "유효하지 않은 userStatus 식별자입니다!");
        Objects.requireNonNull(req, "유효하지 않은 요청입니다!");

        // 유저 ID를 통해 해당 유저의 UserStatus가 있는지 검증
        Optional<UserStatus> optUserStatus = userStatusRepository.findByUserId(userId);
        if (optUserStatus.isEmpty()) {
            throw new NoSuchElementException("해당 UserStatus 찾을 수 없음!");
        }
        UserStatus userStatus = optUserStatus.get();

        // 엔티티에 직접 접근해서 업데이트 후 영속성 컨텍스트에서 변경 감지 -> DirtyChecking으로 update 쿼리문이 나감
        userStatus.update(req.newLastActiveAt());

        return UserStatusDTOMapper.toDto(userStatus);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        Objects.requireNonNull(id, "유효하지 않은 ID 입니다!");
        userStatusRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("해당 UserStatus를 찾을 수 없음."));
        userStatusRepository.deleteById(id);
    }

}
