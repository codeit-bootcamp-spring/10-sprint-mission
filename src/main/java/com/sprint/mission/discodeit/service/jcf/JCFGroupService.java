package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Group;
import com.sprint.mission.discodeit.service.GroupService;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

//신규 클래스: 그룹
public class JCFGroupService implements GroupService {
    private static JCFGroupService instance = null;
    private JCFGroupService(){}
    public static JCFGroupService getInstance() {
        if(instance == null) instance = new JCFGroupService();
        return instance;
    }

    Set<Group> groups = new HashSet<>();

    @Override
    public Group find(UUID id) {
        return groups.stream()
                .filter(group -> id.equals(group.getId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Group not found: id = " + id));
    }

    @Override
    public Set<Group> findAll() {
        Set<Group> newGroups = new HashSet<>();
        newGroups.addAll(groups);
        return newGroups;
    }

    @Override
    public Group create(String groupName) {
        Group group = new Group(groupName);
        groups.add(group);
        return group;
    }

    @Override
    public void delete(UUID id) {
        groups.remove(find(id));
    }

    @Override
    public Group updateGroupName(UUID id, String GroupName) {
        Group group = this.find(id);
        group.updateGroupName(GroupName);
        return group;
    }


}
