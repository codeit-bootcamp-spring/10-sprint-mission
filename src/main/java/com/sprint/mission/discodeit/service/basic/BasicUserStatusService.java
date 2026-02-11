package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.userStatus.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.userStatus.UserStatusResponse;
import com.sprint.mission.discodeit.dto.userStatus.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class BasicUserStatusService implements UserStatusService {
    private final UserStatusRepository userStatusRepository;
    private final UserRepository userRepository;

    @Override
    public UserStatusResponse create(UserStatusCreateRequest request) {
        UserStatus userStatus = new UserStatus(request.userID());
        UserStatus newUserStatus = userStatusRepository.save(userStatus);
        return new UserStatusResponse(newUserStatus.getUserID(), newUserStatus.isOnline());
    }

    @Override
    public UserStatusResponse find(UUID userID){
        UserStatus userStatus = userStatusRepository.findByUserID(userID)
                .orElseThrow(() -> new IllegalArgumentException("UserStatus not found: " + userID));
        return new UserStatusResponse(userStatus.getUserID(), userStatus.isOnline());
    }

    @Override
    public List<UserStatusResponse> findAll(){

        return null;
    }

    @Override
    public UserStatusResponse update(UserStatusUpdateRequest request) {
        UserStatus userStatus = userStatusRepository.find(request.userStatusID())
                .orElseThrow(() -> new IllegalArgumentException("UserStatus not found: " + request.userStatusID()));
        userStatus.updateLastLogin();
        UserStatus newUserStatus = userStatusRepository.save(userStatus);
        return new UserStatusResponse(newUserStatus.getUserID(), newUserStatus.isOnline());
    }

    @Override
    public UserStatusResponse updateByUserID(UUID userID) {
        UserStatus userStatus = userStatusRepository.findByUserID(userID)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userID));
        userStatus.updateLastLogin();
        UserStatus newUserStatus = userStatusRepository.save(userStatus);
        return new UserStatusResponse(newUserStatus.getUserID(), newUserStatus.isOnline());
    }

    @Override
    public void delete(UUID userStatusID) {
        UserStatus userStatus = userStatusRepository.find(userStatusID)
                .orElseThrow(() -> new IllegalArgumentException("UserStatus not found: " + userStatusID));
        userStatusRepository.deleteUserStatus(userStatus.getId());
    }
}
