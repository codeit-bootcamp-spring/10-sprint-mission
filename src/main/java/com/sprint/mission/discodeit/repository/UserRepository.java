package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;

import java.util.Set;
import java.util.UUID;

public interface UserRepository {
    public void fileSave(Set<User> users);
    public Set<User> fileLoadAll();
    public User fileLoad(UUID id);
    public void fileDelete(UUID id);
}
