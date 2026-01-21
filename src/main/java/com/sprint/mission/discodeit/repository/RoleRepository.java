package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Role;

import java.util.Set;
import java.util.UUID;

public interface RoleRepository {
    public void fileSave(Set<Role> roles);
    public Set<Role> fileLoad();
    public void fileDelete(UUID id);
}
