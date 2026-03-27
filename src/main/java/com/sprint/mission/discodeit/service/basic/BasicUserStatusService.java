package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.userstatusdto.UserStatusDto;
import com.sprint.mission.discodeit.dto.userstatusdto.UserStatusUpdateRequestDTO;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.UserStatusDTOMapper;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.UUID;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BasicUserStatusService implements UserStatusService {

    private final UserStatusRepository userStatusRepository;
    private final UserStatusDTOMapper userStatusDTOMapper;

    @Override
    public UserStatusDto find(UUID id) {
        Objects.requireNonNull(id, "유효하지 않은 ID 입니다!");
        log.trace("[UserStatus] 조회 메서드 시작: id={}", id);

        UserStatus userStatus = userStatusRepository.findById(id)
            .orElseThrow(
                () -> new NoSuchElementException("해당 UserStatus를 찾을 수 없습니다!"));
        log.debug("[UserStatus] 조회된 UserStatus 정보: id={}, userId={}"
            , userStatus.getId(), userStatus.getUser().getId()
        );

        UserStatusDto userStatusDto = userStatusDTOMapper.toDto(userStatus);
        log.info("[UserStatus] UserStatus 조회 성공");
        return userStatusDto;
    }

    @Override
    @Transactional
    public UserStatusDto activateUserOnline(UUID userId, UserStatusUpdateRequestDTO req) {
        Objects.requireNonNull(userId, "유효하지 않은 사용자 ID!");
        Objects.requireNonNull(req, "유효하지 않은 생성 요청!");

        log.trace("[UserStatus] UserStatus Activate Online 메서드 시작: id={}, newLastActiveAt={}"
            , userId, req.newLastActiveAt());

        Optional<UserStatus> optUserStatus = userStatusRepository.findByUserId(userId);
        if (optUserStatus.isEmpty()) {
            throw new NoSuchElementException("해당 UserStatus 찾을 수 없습니다!");
        }

        UserStatus userStatus = optUserStatus.get();
        log.debug("[UserStatus] 수정하고자 하는 UserStatus 정보: id={}, userId={}",
            userStatus.getId(), userStatus.getUser().getId());

        userStatus.update(req.newLastActiveAt());
        log.info("[UserStatus] 유저 업데이트 성공");

        return userStatusDTOMapper.toDto(userStatus);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        Objects.requireNonNull(id, "유효하지 않은 ID 입니다!");
        log.trace("UserStatus 삭제 메서드 시작: id={}", id);

        UserStatus userStatus = userStatusRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("해당 UserStatus를 찾을 수 없습니다!"));
        userStatusRepository.deleteById(id);

        log.info("UserStatus 삭제 성공: id={}, userId={}",
            userStatus.getId(), userStatus.getUser().getId());
    }

}
