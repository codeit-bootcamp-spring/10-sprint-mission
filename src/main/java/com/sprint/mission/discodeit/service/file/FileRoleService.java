package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.PermissionLevel;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.file.FileRoleRepository;
import com.sprint.mission.discodeit.service.RoleService;

import java.io.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class FileRoleService implements RoleService {
    private FileRoleRepository repository = new FileRoleRepository();

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
        return repository.fileLoad();
    }

    @Override
    public Role create(PermissionLevel roleName, UUID userID, UUID channelID) {
        User user = new FileUserService().find(userID);
        Channel channel = new FileChannelService().find(channelID);

        Role role = new Role(roleName, user, channel);
        Set<Role> usersInFile = findAll();
        usersInFile.add(role);
        repository.fileSave(usersInFile);//자체 리스트 파일에 등록하고

        user.getRoles().add(role); //유저에 등록하며
        channel.getRoles().add(role); //채널에도 등록

        new FileUserService().update(user.getId(), user.getRoles());
        new FileChannelService().update(channel.getId(), channel.getRoles(), channel.getMessages());

        return role;
    }

    @Override
    public void delete(UUID id) {
        Role role = find(id);
        User user = role.getUsers();
        Channel channel = role.getChannel();

        // 1. Role 저장소에서 삭제
        Set<Role> rolesInFile = findAll();
        rolesInFile.removeIf(r -> r.getId().equals(id));
        repository.fileSave(rolesInFile);

        // 2. 연관된 User에서 Role 제거 및 업데이트
        if (user != null) {
            user.getRoles().removeIf(r -> r.getId().equals(id));
            new FileUserService().update(user.getId(), user.getRoles());
        }

        // 3. 연관된 Channel에서 Role 제거 및 업데이트
        if (channel != null) {
            channel.getRoles().removeIf(r -> r.getId().equals(id));
            new FileChannelService().update(channel.getId(), channel.getRoles(), channel.getMessages());
        }
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

        User user = role.getUsers();
        Channel channel = role.getChannel();

        if (user != null) {
            // User가 가진 Role 목록 중 현재 수정된 Role을 찾아 업데이트 (Set이므로 기존 것 삭제 후 추가 혹은 참조 업데이트)
            user.getRoles().removeIf(r -> r.getId().equals(roleID));
            user.getRoles().add(role);
            new FileUserService().update(user.getId(), user.getRoles());
        }

        if (channel != null) {
            // Channel이 가진 Role 목록 중 현재 수정된 Role을 찾아 업데이트
            channel.getRoles().removeIf(r -> r.getId().equals(roleID));
            channel.getRoles().add(role);
            new FileChannelService().update(channel.getId(), channel.getRoles(), channel.getMessages());
        }

        return role;
    }
}
