package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;
import java.util.stream.Collectors;

public class JCFUserService implements UserService {

    private final Map<UUID, User> data;
    //set, map, list 모두 가능하지만, 해당 요소를 빨리 찾기 위해서는 맵이 가장 적절하다고 판단
    public JCFUserService() { data = new HashMap<>();}

    @Override
    public User signUp(String userName,String email, String password) {
        validateEmail(email);
        User user = new User(userName, email, password);
        data.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateName(UUID id, String name) {
        User user = getUserById(id);
        user.setUserName(name);
        return user;
    }

    @Override
    public User getUserById(UUID id) {
        validateUser(id);
        return data.get(id);
    }

    @Override
    public List<User> findAllUsers() {
        List<User> users = new ArrayList<>(data.values());
        return users;
    }

    @Override
    public void removeUserById(UUID id) {
        User user = getUserById(id);
        user.removeAllChannels();
        user.setUserName("[삭제된 사용자]");
        //소유 채널 삭제 필요
        data.remove(id);
    }


    @Override
    public List<Channel> getChannels(UUID id) {
        User user = getUserById(id);
        return new ArrayList<>(user.getChannels());
    }

    private void validateEmail(String email) {
        boolean exists = data.values()
                .stream()
                .anyMatch(u -> u.getEmail().equals(email));
        if (exists) {
            throw new IllegalArgumentException("이미 존재하는 이메일: "+email);
        }
    }

    private void validateUser(UUID id) {
        boolean exists = data.containsKey(id);
        if (!exists) {
            throw new NoSuchElementException("유효하지 않은 사용자ID: "+id);
        }
    }
}
