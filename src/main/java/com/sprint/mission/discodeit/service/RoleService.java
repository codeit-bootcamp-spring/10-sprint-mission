package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Role;

import java.util.Set;
import java.util.UUID;

public interface RoleService {
    public Role find(UUID id);
    public Set<Role> findAll();
    public Role create(String groupName, UUID userID, UUID channelID);
    public void delete(UUID id);
    public Role updateGroupName(UUID id, String str);
}
