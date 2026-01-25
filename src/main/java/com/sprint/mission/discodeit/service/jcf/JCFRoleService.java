package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.PermissionLevel;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.RoleService;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

//신규 클래스: 그룹
public class JCFRoleService implements RoleService {
    private static JCFRoleService instance = null;
    private JCFRoleService(){}
    public static JCFRoleService getInstance() {
        if(instance == null) instance = new JCFRoleService();
        return instance;
    }

    Set<Role> groups = new HashSet<>();

    @Override
    public Role find(UUID id) {
        return groups.stream()
                .filter(group -> id.equals(group.getId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Group not found: id = " + id));
    }

    @Override
    public Set<Role> findAll() {
        Set<Role> newGroups = new HashSet<>();
        newGroups.addAll(groups);
        return newGroups;
    }

    @Override
    public Role create(PermissionLevel roleName, UUID userID, UUID channelID) {
        User user = JCFUserService.getInstance().find(userID);
        Channel channel = JCFChannelService.getInstance().find(channelID);
        Role role = new Role(roleName, userID, channelID);

        groups.add(role); //자체 리스트에 추가하고
        user.getRoleIDs().add(role.getId()); //유저에 등록하며
        channel.getRolesID().add(role.getId()); //채널에도 등록

        return role;
    }

    @Override
    public void delete(UUID id) {
        Role role = find(id);
        JCFChannelService.getInstance()
                .find(role.getChannelID())
                .getRolesID()
                .remove(id);
        JCFUserService.getInstance().find(role.getUserID())
                .getRoleIDs().remove(id);
        groups.remove(role);
    }

    public Role update(UUID roleID, PermissionLevel roleName) {
        Role role = this.find(roleID);
        role.updateGroupName(roleName);
        return role;
    }

}