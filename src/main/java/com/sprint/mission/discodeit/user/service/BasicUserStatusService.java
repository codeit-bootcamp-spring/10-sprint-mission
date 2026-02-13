package com.sprint.mission.discodeit.user.service;

import com.sprint.mission.discodeit.user.entity.UserStatus;
import com.sprint.mission.discodeit.user.dto.UserStatusCreateRequest;
import com.sprint.mission.discodeit.user.dto.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.user.entity.User;
import com.sprint.mission.discodeit.user.repository.UserRepository;
import com.sprint.mission.discodeit.user.repository.UserStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicUserStatusService implements UserStatusService {
    private final UserStatusRepository userStatusRepository;
    private final UserRepository userRepository;

    @Override
    public void create(UserStatusCreateRequest request) {
        User user = userRepository.findById(request.userId())
                .orElseThrow(() ->new IllegalArgumentException("존재하지 않는 유저입니다."));

        userStatusRepository.findByUserId(request.userId())
                .ifPresent(status -> {
                    throw new IllegalArgumentException("해당 유저의 접속 상태 객체가 이미 존재합니다.");
                });
        UserStatus userStatus = new UserStatus(request.userId());
        userStatusRepository.save(userStatus);
    }

    @Override
    public UserStatus find(UUID userStatusID) {
        return userStatusRepository.findById(userStatusID)
                .orElseThrow(()-> new IllegalArgumentException("해당 접속 상태 객체를 찾을 수 없습니다."));
    }

    @Override
    public List<UserStatus> findAll() {
        return userStatusRepository.findAll();
    }

    @Override
    public void update(UserStatusUpdateRequest request) {
        UserStatus userStatus = this.find(request.id());
        userStatus.updateConnection();
        userStatusRepository.save(userStatus);
    }

    @Override
    public void updateByUserId(UUID userId) {
        UserStatus userStatus = userStatusRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저의 접속 상태 객체를 찾을 수 없습니다."));

        userStatus.updateConnection();
        userStatusRepository.save(userStatus);
    }

    @Override
    public void delete(UUID userStatusId) {
        this.find(userStatusId);
        userStatusRepository.deleteById(userStatusId);
    }
}

