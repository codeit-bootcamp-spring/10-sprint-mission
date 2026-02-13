package com.sprint.mission.discodeit.userstatus.service;

import com.sprint.mission.discodeit.user.exception.UserNotFoundException;
import com.sprint.mission.discodeit.userstatus.dto.UserStatusCreateInfo;
import com.sprint.mission.discodeit.userstatus.dto.UserStatusInfo;
import com.sprint.mission.discodeit.userstatus.dto.UserStatusUpdateInfo;
import com.sprint.mission.discodeit.userstatus.entity.UserStatus;
import com.sprint.mission.discodeit.userstatus.mapper.UserStatusMapper;
import com.sprint.mission.discodeit.user.repository.UserRepository;
import com.sprint.mission.discodeit.userstatus.exception.UserStatusDuplicationException;
import com.sprint.mission.discodeit.userstatus.exception.UserStatusNotFoundException;
import com.sprint.mission.discodeit.userstatus.repository.UserStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class UserStatusService {
    private final UserStatusRepository userStatusRepository;
    private final UserRepository userRepository;

    public UserStatusInfo createUserStatus(UserStatusCreateInfo statusInfo) {
        userRepository.findById(statusInfo.userId())
                .orElseThrow(UserNotFoundException::new);
        if (userStatusRepository.findByUserId(statusInfo.userId())
                .isPresent()) throw new UserStatusDuplicationException();
        UserStatus userStatus = new UserStatus(statusInfo.userId());
        userStatusRepository.save(userStatus);
        return UserStatusMapper.toUserStatusInfo(userStatus);
    }

    public UserStatusInfo findUserStatus(UUID statusId) {
        UserStatus userStatus = userStatusRepository.findById(statusId)
                .orElseThrow(UserStatusNotFoundException::new);
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
                .orElseThrow(UserStatusNotFoundException::new);
        userStatus.updateLastOnlineAt();
        userStatusRepository.save(userStatus);
        return UserStatusMapper.toUserStatusInfo(userStatus);
    }

    public UserStatusInfo updateUserStatusByUserId(UUID userId) {
        UserStatus userStatus = userStatusRepository.findByUserId(userId)
                .orElseThrow(UserStatusNotFoundException::new);
        userStatus.updateLastOnlineAt();
        userStatusRepository.save(userStatus);
        return UserStatusMapper.toUserStatusInfo(userStatus);
    }

    void deleteUserStatus(UUID statusId) {
        userStatusRepository.deleteById(statusId);
    }
}
