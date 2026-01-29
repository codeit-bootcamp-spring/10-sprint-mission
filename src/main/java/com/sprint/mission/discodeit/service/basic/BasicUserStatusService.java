package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.UserStatusDto;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicUserStatusService implements UserStatusService {
    private final UserStatusRepository userStatusRepository;
    private final UserRepository userRepository;

    @Override
    public UserStatus create(UserStatusDto.CreateRequest request) {
        UUID userId = request.userid();
        Instant lastActiveAt = request.lastActiveAt();

        if (!userRepository.existsById(userId)) {
            throw new NoSuchElementException("해당 유저를 찾을 수 없습니다:" + userId);
        }
        boolean isExits = userStatusRepository.findByUserId(userId).isPresent();
        if (isExits) {
            throw new IllegalArgumentException("이미 해당 유저의 UserStatus가 있습니다.");
        }

        UserStatus userStatus = new UserStatus(userId, lastActiveAt);

        return userStatusRepository.save(userStatus);


    }

    @Override
    public UserStatus find(UUID userStatusId) {
        return userStatusRepository.findById(userStatusId)
                .orElseThrow(() -> new NoSuchElementException("해당 UserStatus를 찾을 수 없습니다:" + userStatusId));
    }

    @Override
    public List<UserStatus> findAll() {
        return userStatusRepository.findAll();
    }

    @Override
    public UserStatus update(UUID userStatusId, UserStatusDto.UpdateRequest request) {
        UserStatus userStatus = userStatusRepository.findById(userStatusId)
                .orElseThrow(() -> new NoSuchElementException("해당 UserStatus를 찾을 수 없습니다:" + userStatusId));
        userStatus.update(request.newLastActiveAt());

        return userStatusRepository.save(userStatus);
    }

    @Override
    public UserStatus updateByUserId(UUID userId, UserStatusDto.UpdateRequest request) {
        UserStatus userStatus = userStatusRepository.findByUserId(userId)
                .orElseThrow(() -> new NoSuchElementException("해당 UserStatus를 찾을 수 없습니다:" + userId));
        userStatus.update(request.newLastActiveAt());

        return userStatusRepository.save(userStatus);
    }

    @Override
    public void delete(UUID userStatusId) {
        if (!userStatusRepository.existsById(userStatusId)) {
            throw new NoSuchElementException("해당 UserStatus를 찾을 수 없습니다:" + userStatusId);
        }
        userStatusRepository.deleteById(userStatusId);
    }
}
