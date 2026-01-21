package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.repository.RoleRepository;

import java.io.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class FileRoleRepository implements RoleRepository {
    public static final String FILE_PATH = "roles.dat";

    @Override
    public void fileSave(Set<Role> roles) {
        try (ObjectOutputStream fileOutput = new ObjectOutputStream(new FileOutputStream(FILE_PATH))){
            fileOutput.writeObject(roles);
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public Set<Role> fileLoad() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            return new HashSet<>(); // 파일이 없으면 빈 셋 반환
        }

        try (ObjectInputStream fileInput = new ObjectInputStream(new FileInputStream(FILE_PATH))) {
            return (Set<Role>)fileInput.readObject();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void fileDelete(UUID id) {
        Set<Role> roles = fileLoad();
        roles.removeIf(role -> role.getId().equals(id));
        fileSave(roles);
    }

}
