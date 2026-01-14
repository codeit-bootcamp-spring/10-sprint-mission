package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.RoleGroup;
import com.sprint.mission.discodeit.service.RoleGroupService;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

//신규 클래스: 그룹
public class JCFRoleGroupService implements RoleGroupService {
    private static JCFRoleGroupService instance = null;
    private JCFRoleGroupService(){}
    public static JCFRoleGroupService getInstance() {
        if(instance == null) instance = new JCFRoleGroupService();
        return instance;
    }

    Set<RoleGroup> groups = new HashSet<>();

    @Override
    public RoleGroup find(UUID id) {
        return groups.stream()
                .filter(group -> id.equals(group.getId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Group not found: id = " + id));
    }

    @Override
    public Set<RoleGroup> findAll() {
        Set<RoleGroup> newGroups = new HashSet<>();
        newGroups.addAll(groups);
        return newGroups;
    }

    @Override
    public RoleGroup create(String groupName) {
        RoleGroup group = new RoleGroup(groupName);
        groups.add(group);
        return group;
    }

    @Override
    public void delete(UUID id) {
        groups.remove(find(id));
    }

    @Override
    public RoleGroup updateGroupName(UUID id, String GroupName) {
        RoleGroup group = this.find(id);
        group.updateGroupName(GroupName);
        return group;
    }


}
