package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.DuplicationEmailException;
import com.sprint.mission.discodeit.exception.UserNotFoundException;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFUserService implements UserService {

    private final Map<UUID, User> users = new LinkedHashMap<>();

    @Override
    public User createUser(String userName, String userEmail) {
        if (users.values().stream()
                .anyMatch(u -> u.getUserEmail().equals(userEmail))) {
            throw new DuplicationEmailException();
        }

        User user = new User(userName, userEmail);
        users.put(user.getId(), user);

        return user;
    }

    @Override
    public User findUser(UUID userId) {
        User user = users.get(userId);
        if (user == null) throw new UserNotFoundException();
        return user;
    }

    @Override
    public List<User> findAllUser() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User updateUser(UUID userId, String userName, String userEmail) {
        User user = findUser(userId);
        user.update(userName, userEmail);
        return user;
    }

    @Override
    public User deleteUser(UUID userId) {
        User removed = users.remove(userId);

        if (removed == null) {
            throw new UserNotFoundException();
        }
        return removed;
    }
}