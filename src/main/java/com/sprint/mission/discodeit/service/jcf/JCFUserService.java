package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFUserService implements UserService {
    private final HashMap<UUID, User> data;

    public JCFUserService() {
        this.data = new HashMap<>();
    }
    public JCFUserService(User user) {
        this.data = new HashMap<>();
        data.put(user.getId(), user);
    }

    @Override
    public void create() {
        User user = new User();
        data.put(user.getId(), user);
    }

    @Override
    public Optional<User> read(UUID id) {
        if (data.isEmpty() || !data.containsKey(id)) {
            System.out.println("해당 유저는 존재하지 않습니다.");
        }
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public Optional<ArrayList<User>> readAll() {
        ArrayList<User> list = list = new ArrayList<>(data.values());;
        if (data.isEmpty()) {
            System.out.println("유저 데이터가 존재하지 않습니다.");
        }
        return Optional.ofNullable(list);
    }

    @Override
    public void update(User userData) {
        this.data.put(userData.getId(), userData);
    }

    @Override
    public User delete(UUID id) {
        return data.remove(id);
    }
}
