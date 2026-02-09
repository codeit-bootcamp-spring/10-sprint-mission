package com.sprint.mission.discodeit.userstatus.service;

import com.sprint.mission.discodeit.userstatus.dto.UserStatusCreateInfo;
import com.sprint.mission.discodeit.userstatus.dto.UserStatusInfo;
import com.sprint.mission.discodeit.userstatus.dto.UserStatusUpdateInfo;
import com.sprint.mission.discodeit.userstatus.UserStatus;
import com.sprint.mission.discodeit.userstatus.UserStatusMapper;
import com.sprint.mission.discodeit.user.repository.UserRepository;
import com.sprint.mission.discodeit.userstatus.repository.UserStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class UserStatusService {
    private final UserStatusRepository userStatusRepository;
    private final UserRepository userRepository;

    public UserStatusInfo createUserStatus(UserStatusCreateInfo statusInfo) {
        userRepository.findById(statusInfo.userId())
                .orElseThrow(() -> new NoSuchElementException("해당 사용자가 존재하지 않습니다."));
        if (userStatusRepository.findByUserId(statusInfo.userId())
                .isPresent()) throw new IllegalStateException("유저 상태가 이미 존재합니다.");
        UserStatus userStatus = new UserStatus(statusInfo.userId());
        userStatusRepository.save(userStatus);
        return UserStatusMapper.toUserStatusInfo(userStatus);
    }

    public UserStatusInfo findUserStatus(UUID statusId) {
        UserStatus userStatus = userStatusRepository.findById(statusId)
                .orElseThrow(() -> new NoSuchElementException("해당 사용자 상태가 존재하지 않습니다."));
        return UserStatusMapper.toUserStatusInfo(userStatus);
    }

    public List<UserStatusInfo> findAll() {
        return userStatusRepository.findAll()
                .stream()
                .map(UserStatusMapper::toUserStatusInfo)
                .toList();
    }

    public UserStatusInfo updateUserStatus(UserStatusUpdateInfo statusInfo) {
        UserStatus userStatus = userStatusRepository.findById(statusInfo.statusId())
                .orElseThrow(() -> new NoSuchElementException("해당 사용자 상태가 존재하지 않습니다."));
        userStatus.updateLastOnlineAt();
        userStatusRepository.save(userStatus);
        return UserStatusMapper.toUserStatusInfo(userStatus);
    }

    public UserStatusInfo updateUserStatusByUserId(UUID userId) {
        UserStatus userStatus = userStatusRepository.findByUserId(userId)
                .orElseThrow(() -> new NoSuchElementException("해당 사용자 상태가 존재하지 않습니다."));
        userStatus.updateLastOnlineAt();
        userStatusRepository.save(userStatus);
        return UserStatusMapper.toUserStatusInfo(userStatus);
    }

    void deleteUserStatus(UUID statusId) {
        userStatusRepository.deleteById(statusId);
    }
}
