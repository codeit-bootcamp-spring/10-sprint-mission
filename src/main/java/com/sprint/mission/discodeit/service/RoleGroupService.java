package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.RoleGroup;

import java.util.Set;
import java.util.UUID;

public interface RoleGroupService {
    public RoleGroup find(UUID id);
    public Set<RoleGroup> findAll();
    public RoleGroup create(String groupName);
    public void delete(UUID id);
    public RoleGroup updateGroupName(UUID id, String str);
}
