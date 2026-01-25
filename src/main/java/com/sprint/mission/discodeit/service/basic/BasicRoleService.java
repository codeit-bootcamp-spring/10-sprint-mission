package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.PermissionLevel;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.RoleRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.RoleService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.Set;
import java.util.UUID;

public class BasicRoleService implements RoleService {
    private RoleRepository repository;
    private UserService userService;
    private ChannelService channelService;

    public BasicRoleService(RoleRepository repository,
                            UserService userService,
                            ChannelService channelService
    ) {
        this.repository = repository;
        this.userService = userService;
        this.channelService = channelService;
    }

    @Override
    public Role find(UUID id) {
        Set<Role> usersInFile = findAll();
        return usersInFile.stream()
                .filter(user -> user.getId().equals(id))
                .findFirst()
                .orElseThrow(() ->new RuntimeException("User not found: id = " + id));
    }

    @Override
    public Set<Role> findAll() {
        return repository.fileLoadAll();
    }

    @Override
    public Role create(PermissionLevel roleName, UUID userID, UUID channelID) {
        User user = userService.find(userID);
        Channel channel = channelService.find(channelID);

        Role role = new Role(roleName, userID, channelID);
        Set<Role> usersInFile = findAll();
        usersInFile.add(role);
        repository.fileSave(usersInFile);//자체 리스트 파일에 등록

        user.AddRoleInUser(role.getId()); //불러온 유저에 등록하며
        channel.AddRoleInChannel(role.getId()); //불러온 채널에도 등록

        userService.update(user.getId(), user.getRoleIDs());//저장파일에 유저 전송하고
        channelService.update(channel.getId(), channel.getRolesID(), channel.getMessagesID());//채널에도 전송하기

        return role;
    }

    @Override
    public void delete(UUID id) {
        Role role = find(id);
        User user = userService.find(role.getUserID());
        Channel channel = channelService.find(role.getChannelID());

        // User에서 Role 제거 및 갱신
        if (user != null) {
            user.getRoleIDs()
                    .removeIf(r -> r.equals(id));
            userService.update(user.getId(), user.getRoleIDs());
        }

        // Channel에서 Role 제거 및 갱신
        if (channel != null) {
            channel.getRolesID().removeIf(r -> r.equals(id));
            channelService.update(channel.getId(), channel.getRolesID(), channel.getMessagesID());
        }

        // Role 저장소에서 삭제
        Set<Role> rolesInFile = findAll();
        rolesInFile.removeIf(r -> r.getId().equals(id));
        repository.fileSave(rolesInFile);
    }

    @Override
    public Role update(UUID roleID, PermissionLevel roleName) {
        Set<Role> rolesInFile = findAll();

        Role role = rolesInFile.stream()
                .filter(u -> u.getId().equals(roleID))
                .findFirst()
                .orElseThrow(() ->new RuntimeException("User not found: id = " + roleID));

        role.updateGroupName(roleName);
        repository.fileSave(rolesInFile);

        return role;
    }
}
