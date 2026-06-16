package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.UserStatusDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.event.UserEvents;
import com.sprint.mission.discodeit.exception.etc.InternalServerException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.exception.userstatus.UserStatusAlreadyExistsException;
import com.sprint.mission.discodeit.exception.userstatus.UserStatusNotFoundException;
import com.sprint.mission.discodeit.mapper.UserStatusMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

/**
 * 사용자의 온라인 상태 및 활동 정보를 관리하는 기본 서비스 클래스입니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BasicUserStatusService implements UserStatusService {
    private final UserStatusRepository userStatusRepository;
    private final UserRepository userRepository;
    private final UserStatusMapper userStatusMapper;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * 새로운 사용자 상태 정보를 생성합니다.
     *
     * @param request 사용자 상태 생성 요청 정보
     * @return 생성된 상태 상세 정보
     */
    @Override
    @Transactional
    public UserStatusDto.Response create(UserStatusDto.CreateRequest request) {
        UUID userId = request.userId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> UserNotFoundException.withId(userId));

        if (userStatusRepository.existsByUserId(userId)) {
            throw UserStatusAlreadyExistsException.withUserId(userId);
        }

        UserStatus userStatus = new UserStatus(user, request.lastActiveAt() != null
                ? request.lastActiveAt()
                : Instant.now());

        try {
            UserStatus savedStatus = userStatusRepository.saveAndFlush(userStatus);
            log.info("[UserStatus] 신규 상태 정보 생성: UserId={}", userId);
            
            eventPublisher.publishEvent(new UserEvents.StatusUpdated(userId));
            return userStatusMapper.toResponse(savedStatus);
        } catch (DataIntegrityViolationException e) {
            // 레이스 컨디션 발생 시 기존 데이터 조회하여 반환
            UserStatus existingUserStatus = userStatusRepository.findByUserId(userId)
                    .orElseThrow(() -> InternalServerException.dataIntegrity("UserStatus 존재해야 함에도 찾을 수 없음: User %s", userId));
            return userStatusMapper.toResponse(existingUserStatus);
        }
    }

    /**
     * 상태 정보를 ID로 조회합니다.
     */
    @Override
    public UserStatusDto.Response find(UUID userStatusId) {
        return userStatusRepository.findById(userStatusId)
                .map(userStatusMapper::toResponse)
                .orElseThrow(() -> UserStatusNotFoundException.withId(userStatusId));
    }

    /**
     * 특정 사용자의 상태 정보를 조회합니다.
     */
    @Override
    public UserStatusDto.Response findByUserId(UUID userId) {
        validateUserExists(userId);
        return userStatusRepository.findByUserId(userId)
                .map(userStatusMapper::toResponse)
                .orElseThrow(() -> InternalServerException.dataIntegrity("UserStatus 존재해야 함: User %s", userId));
    }

    /**
     * 모든 사용자의 상태 정보를 조회합니다.
     */
    @Override
    public List<UserStatusDto.Response> findAll() {
        return userStatusRepository.findAll().stream()
                .map(userStatusMapper::toResponse)
                .toList();
    }

    /**
     * 상태 정보를 업데이트합니다. (ID 기반)
     */
    @Override
    @Transactional
    public UserStatusDto.Response update(UUID userStatusId, UserStatusDto.UpdateRequest request) {
        UserStatus userStatus = userStatusRepository.findById(userStatusId)
                .orElseThrow(() -> UserStatusNotFoundException.withId(userStatusId));
        
        return performUpdate(userStatus, request.newLastActiveAt());
    }

    /**
     * 특정 사용자의 상태 정보를 업데이트합니다. (UserId 기반)
     */
    @Override
    @Transactional
    public UserStatusDto.Response updateByUserId(UUID userId, UserStatusDto.UpdateRequest request) {
        validateUserExists(userId);
        UserStatus userStatus = userStatusRepository.findByUserId(userId)
                .orElseThrow(() -> InternalServerException.dataIntegrity("UserStatus 존재해야 함: User %s", userId));

        return performUpdate(userStatus, request.newLastActiveAt());
    }

    /**
     * 상태 정보를 삭제합니다.
     */
    @Override
    @Transactional
    public void delete(UUID userStatusId) {
        UserStatus userStatus = userStatusRepository.findById(userStatusId)
                .orElseThrow(() -> UserStatusNotFoundException.withId(userStatusId));

        userStatusRepository.delete(userStatus);
        log.info("[UserStatus] 상태 정보 삭제: ID={}, UserId={}", userStatusId, userStatus.getUser().getId());
        
        eventPublisher.publishEvent(new UserEvents.StatusUpdated(userStatus.getUser().getId()));
    }

    // --- Private Helpers ---

    private UserStatusDto.Response performUpdate(UserStatus userStatus, Instant newLastActiveAt) {
        userStatus.update(newLastActiveAt);
        UserStatus updatedStatus = userStatusRepository.save(userStatus);
        
        log.debug("[UserStatus] 활동 시간 업데이트: UserId={}", userStatus.getUser().getId());
        
        eventPublisher.publishEvent(new UserEvents.StatusUpdated(userStatus.getUser().getId()));
        return userStatusMapper.toResponse(updatedStatus);
    }

    private void validateUserExists(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw UserNotFoundException.withId(userId);
        }
    }
}
