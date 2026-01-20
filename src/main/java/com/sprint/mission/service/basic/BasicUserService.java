package com.sprint.mission.service.basic;

import com.sprint.mission.entity.EntityType;
import com.sprint.mission.entity.User;
import com.sprint.mission.repository.UserRepository;
import com.sprint.mission.service.UserService;

public class BasicUserService extends BasicBaseService<User, UserRepository> implements UserService {
    public BasicUserService(UserRepository repository) {
        super(repository, EntityType.USER);
    }

    @Override
    public User create(String userName) {
        User user = new User(userName);
        repository.write(user);
        return user;
    }
}
