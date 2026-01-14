package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Group;

import java.util.Set;
import java.util.UUID;

public interface GroupService {
    public Group find(UUID id);
    public Set<Group> findAll();
    public Group create(String groupName);
    public void delete(UUID id);
    public Group updateGroupName(UUID id, String str);
}
