package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.UserNotFoundException;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.util.*;

public class JCFUserRepository implements UserRepository {
    private final Map<UUID, User> users = new LinkedHashMap<>();

    @Override
    public User createUser(User user) {
        users.put(user.getId(), user);

        return user;
    }

    @Override
    public User findUser(UUID userId) {
        User user = users.get(userId);
        if (user == null) {
            throw new UserNotFoundException();
        }
        return user;
    }

    @Override
    public List<User> findAllUser() {
        return new ArrayList<>(users.values());
    }

    public User updateUser(UUID userId, String userName, String userEmail) {
        User user = findUser(userId);
        user.update(userName, userEmail);
        return user;
    }

    @Override
    public User deleteUser(UUID userId) {
        User removed =  users.remove(userId);
        if (removed == null) {
            throw new UserNotFoundException();
        }
        return removed;
    }
}
