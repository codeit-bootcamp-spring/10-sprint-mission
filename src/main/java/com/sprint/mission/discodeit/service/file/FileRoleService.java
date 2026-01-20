package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.PermissionLevel;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.RoleService;

import java.io.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class FileRoleService implements RoleService {
    private static final String FILE_PATH = "roles.dat";

    public FileRoleService(){
        File file = new File(FILE_PATH);
        if (!file.exists() || file.length() == 0) {
            saveToFile(new HashSet<>());
        }
    }

    public FileRoleService(boolean dummy){//리셋생성자
        saveToFile(new HashSet<>());
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
        try (ObjectInputStream fileInput = new ObjectInputStream(new FileInputStream(FILE_PATH))) {
            return (Set<Role>)fileInput.readObject();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Role create(PermissionLevel roleName, UUID userID, UUID channelID) {
        User user = new FileUserService().find(userID);
        Channel channel = new FileChannelService().find(channelID);

        Role role = new Role(roleName, user, channel);
        Set<Role> usersInFile = findAll();
        usersInFile.add(role);
        saveToFile(usersInFile);//자체 리스트 파일에 등록하고

        user.getRoles().add(role); //유저에 등록하며
        channel.getRoles().add(role); //채널에도 등록

        new FileUserService().update(user);
        new FileChannelService().update(channel);

        return role;
    }

    @Override
    public void delete(UUID id) {
        Set<Role> rolesInFile = findAll();
        rolesInFile.remove(find(id));
        saveToFile(rolesInFile);
    }

    @Override
    public Role update(UUID roleID, PermissionLevel roleName) {
        Set<Role> rolesInFile = findAll();

        Role role = rolesInFile.stream()
                .filter(u -> u.getId().equals(roleID))
                .findFirst()
                .orElseThrow(() ->new RuntimeException("User not found: id = " + roleID));

        rolesInFile.remove(role);
        role.updateGroupName(roleName);
        rolesInFile.add(role);
        saveToFile(rolesInFile);
        return role;
    }

    private void saveToFile(Set<Role> roles){
        try (ObjectOutputStream fileOutput = new ObjectOutputStream(new FileOutputStream(FILE_PATH))){
            fileOutput.writeObject(roles);
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }
}
