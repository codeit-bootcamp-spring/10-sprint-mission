package com.sprint.mission.discodeit.service.jcf;


import com.sprint.mission.discodeit.exception.DuplicationEmailException;
import com.sprint.mission.discodeit.exception.UserNotFoundException;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFUserService implements UserService {

    private final Map<UUID, User> users = new LinkedHashMap<>();

    @Override
    public User createUser(String userName, String userEmail) {
        for (User user : users.values()) {
            if (user.getUserEmail().equals(userEmail)) {
                throw new DuplicationEmailException("이미 존재하는 이메일 입니다.");
            }
        }

        User user = new User(userName, userEmail);
        users.put(user.getId(), user); // BaseEntity 기준 getId()
        return user;
    }

    @Override
    public User findUser(UUID userId) {
        User user = users.get(userId);
        if (user == null) {
            throw new UserNotFoundException("해당되는 회원이 없습니다.");
        }
        return user;
    }

    @Override
    public List<User> findAllUser() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User updateUser(UUID userId, String userName, String userEmail) {
        User user = findUser(userId); // null 체크 포함
        user.update(userName, userEmail);
        return user;
    }

    @Override
    public void deleteUser(UUID userId) {
        User user = users.remove(userId);
        if (user == null) {
            throw new UserNotFoundException("해당되는 회원이 없습니다.");
        }
    }
}
