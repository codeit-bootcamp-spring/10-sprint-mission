package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class JCFUserStatusRepository implements UserStatusRepository {
    private final List<UserStatus> userStatusList;

    public JCFUserStatusRepository() {
        userStatusList = new ArrayList<>();
    }

    @Override
    public UserStatus save(UserStatus userStatus) {
        userStatusList.removeIf(us -> us.getId().equals(userStatus.getId()));
        userStatusList.add(userStatus);
        return userStatus;
    }

    @Override
    public List<UserStatus> findAll() {
        return new ArrayList<>(userStatusList);
    }

    @Override
    public Optional<UserStatus> find(UUID userStatusID) {
        return userStatusList.stream()
                .filter(us -> us.getId().equals(userStatusID))
                .findFirst();
    }

    @Override
    public Optional<UserStatus> findByUserID(UUID userID) {
        return userStatusList.stream()
                .filter(us -> us.getUserID().equals(userID))
                .findFirst();
    }

    @Override
    public void deleteUserStatus(UUID userStatusID) {
        userStatusList.removeIf(us -> us.getId().equals(userStatusID));
    }
}
