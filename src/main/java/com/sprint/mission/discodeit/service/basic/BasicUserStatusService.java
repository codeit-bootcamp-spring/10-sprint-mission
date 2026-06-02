package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.UserStatusDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.etc.InternalServerException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.exception.userstatus.UserStatusAlreadyExistsException;
import com.sprint.mission.discodeit.exception.userstatus.UserStatusNotFoundException;
import com.sprint.mission.discodeit.mapper.UserStatusMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BasicUserStatusService implements UserStatusService {
    private final UserStatusRepository userStatusRepository;
    private final UserRepository userRepository;
    private final UserStatusMapper userStatusMapper;

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

        user.setStatus(userStatus);

        try{ // 레이스 컨디션
            return userStatusMapper.toResponse(userStatusRepository.saveAndFlush(userStatus));
        } catch (DataIntegrityViolationException e) {
            UserStatus existingUserStatus = userStatusRepository.findByUserId(userId)
                    .orElseThrow(() -> InternalServerException.dataIntegrity("UserStatus 존재해야 함에도 찾을 수 없음: User %s", userId));
            return userStatusMapper.toResponse(existingUserStatus);
        }
    }

    @Override
    public UserStatusDto.Response find(UUID userStatusId) {
        return userStatusRepository.findById(userStatusId)
                .map(userStatusMapper::toResponse)
                .orElseThrow(() -> UserStatusNotFoundException.withId(userStatusId));
    }

    @Override
    public UserStatusDto.Response findByUserId(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw UserNotFoundException.withId(userId);
        }

        return userStatusRepository.findByUserId(userId)
                .map(userStatusMapper::toResponse)
                .orElseThrow(() -> InternalServerException.dataIntegrity("UserStatus 존재해야 함: User %s", userId));
    }

    @Override
    public List<UserStatusDto.Response> findAll() {
        return userStatusRepository.findAll().stream()
                .map(userStatusMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public UserStatusDto.Response update(UUID userStatusId, UserStatusDto.UpdateRequest request) {
        UserStatus userStatus = userStatusRepository.findById(userStatusId)
                .orElseThrow(() -> UserStatusNotFoundException.withId(userStatusId));
        userStatus.update(request.newLastActiveAt());

        return userStatusMapper.toResponse(userStatusRepository.save(userStatus));
    }

    @Override
    @Transactional
    public UserStatusDto.Response updateByUserId(UUID userId, UserStatusDto.UpdateRequest request) {
        if (!userRepository.existsById(userId)) {
            throw UserNotFoundException.withId(userId);
        }

        UserStatus userStatus = userStatusRepository.findByUserId(userId)
                .orElseThrow(() -> InternalServerException.dataIntegrity("UserStatus 존재해야 함: User %s", userId));

        userStatus.update(request.newLastActiveAt());

        return userStatusMapper.toResponse(userStatus);
    }

    @Override
    @Transactional
    public void delete(UUID userStatusId) {
        UserStatus userStatus = userStatusRepository.findById(userStatusId)
                .orElseThrow(() -> UserStatusNotFoundException.withId(userStatusId));

        userStatusRepository.delete(userStatus);
    }
}
