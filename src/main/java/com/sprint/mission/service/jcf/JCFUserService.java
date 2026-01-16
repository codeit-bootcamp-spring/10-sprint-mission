package com.sprint.mission.service.jcf;

import com.sprint.mission.entity.User;
import com.sprint.mission.service.UserService;

import java.util.Map;
import java.util.UUID;

public class JCFUserService extends JCFBaseService<User> implements UserService {

    public JCFUserService() {
        super();
    }

    public JCFUserService(Map<UUID, User> data) {
        super(data);
    }

    @Override
    public User create(String userName) {
        User user = new User(userName);
        getData().put(user.getId(), user);
        return user;
    }
}
