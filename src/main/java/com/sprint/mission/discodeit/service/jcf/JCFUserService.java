package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.service.*;
import com.sprint.mission.discodeit.service.listener.*;

import java.util.*;

public class JCFUserService implements UserService {
    private final Map<UUID, User> userDB =  new HashMap<>();

    private final List<UserLifecycleListener> listeners = new ArrayList<>();
    public void addListener(UserLifecycleListener listener) {
        listeners.add(listener);
    }

    private void validateDuplicateUsername(String username) {
        boolean isDuplicate = userDB.values().stream()
                .anyMatch(user -> user.getUsername().equals(username));
        if (isDuplicate) {
            throw new IllegalArgumentException("이미 존재하는 유저이름입니다. (username: " + username + " )");
        }
    }

    @Override
    public User createUser(String username) {
        validateDuplicateUsername(username);
        User user = new User(username);
        userDB.put(user.getId(), user);
        return user;
    }

    @Override
    public User findUserByUserId(UUID userId) {
        if(!userDB.containsKey(userId)) {
            throw new IllegalArgumentException("해당 id의 유저가 존재하지 않습니다. (userId: " + userId + ")");
        }
        return userDB.get(userId);
    }
    @Override
    public List<User> findAllUsers() {
        return new ArrayList<>(userDB.values());
    }

    @Override
    public User updateUser(UUID userId, String newUsername) {
        User user = findUserByUserId(userId);

        if (user.getUsername().equals(newUsername)) {
            return user;
        }

        validateDuplicateUsername(newUsername);
        user.updateUsername(newUsername);
        return user;
    }

    @Override
    public void deleteUser(UUID userId) {
        User user = findUserByUserId(userId);

        for(UserLifecycleListener listener : listeners) {
            listener.onUserDelete(userId);
        }

        userDB.remove(userId);
        System.out.println("[7] 유저(User) 삭제 완료하였습니다." +
                "\n\t유저가 Owner인 채널 삭제([1]~[4])" +
                ", 유저가 작성한 모든 메시지 삭제([5])" +
                ", 유저가 참여하고 있는 모든 채널-유저 관계 삭제([6])");
    }
}