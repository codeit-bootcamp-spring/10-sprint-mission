package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ClearMemory;
import com.sprint.mission.discodeit.service.UserService;

import java.io.*;
import java.util.*;

public class FileUserService extends AbstractFileService implements UserService, ClearMemory {

    public FileUserService(String path) {
        super(path);
    }

    private void save(User user) {
        Map<UUID, User> data = load();

        if(data.containsKey(user.getId())){
            User existing = data.get(user.getId());
            existing.updateName(user.getName());
            existing.updateStatus(user.getStatus());
            existing.updateUpdatedAt(System.currentTimeMillis());
            data.put(existing.getId(), existing);
        }
        else{
            data.put(user.getId(), user);
        }
        writeToFile(data);
    }

    @Override
    public User create(User user) {
        save(user);
        return user;
    }

    @Override
    public User read(UUID id) {
        Map<UUID, User> data = load();
        if(!data.containsKey(id)){
            throw new NoSuchElementException("조회 실패 : 해당 ID의 사용자를 찾을 수 없습니다.");
        }
        return data.get(id);
    }

    @Override
    public List<User> readAll() {
        Map<UUID, User> data = load();
        return List.copyOf(data.values());
    }

    @Override
    public User update(User user) {
        if (read(user.getId()) == null) {
            throw new IllegalArgumentException("존재하지 않는 유저입니다.");
        }
        save(user);
        return user;
    }

    @Override
    public void delete(UUID id) {
        remove(id);
    }

    private void remove(UUID id) {
        Map<UUID, User> data = load();
        if (!data.containsKey(id)) {
            throw new NoSuchElementException("삭제 실패 : 존재하지 않는 사용자 ID입니다.");
        }
        data.remove(id);
        writeToFile(data);
    }

    @Override
    public void clear() {
        writeToFile(new HashMap<UUID, User>());
    }
}
