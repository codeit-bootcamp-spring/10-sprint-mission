package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFUserService implements UserService {
    private final HashMap<UUID, User> data;

    public JCFUserService() {
        this.data = new HashMap<>();
    }

    @Override
    public User create(String name) {
        User user = new User(name);
        data.put(user.getId(), user);
        return user;
    }

    @Override
    public User read(UUID id) {
        User user = Optional.ofNullable(data.get(id)).orElseThrow(() -> new NoSuchElementException());
        return user;
    }

    @Override
    public List<User> readAll() {
        return new ArrayList<>(this.data.values());
    }

    @Override
    public User update(UUID id, String name) {
        this.read(id).updateName(name);
        return this.read(id);
    }

    @Override
    public void delete(UUID id) {
        this.read(id);
        this.data.remove(id);
    }

    @Override
    public List<User> readChannelUserList(Channel channel) {
        return channel.getUserList();
    }
}
