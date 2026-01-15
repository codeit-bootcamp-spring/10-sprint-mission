package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

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
    public User updateInfo(UUID id, String userName, String email, String password) {
        User user = getUserById(id);
        //요청한 값이 널 또는 이전과 같은 값들로만 구성된 경우
        boolean unChanged = (userName ==null || userName.equals(user.getUserName()))
                && (email ==null || email.equals(user.getEmail()))
                && (password ==null || password.equals(user.getPassword()));
        if(unChanged){
            throw new IllegalArgumentException("변경사항 없음");
        }
        Optional.ofNullable(userName)
                .filter(n -> !n.equals(user.getUserName()))
                .ifPresent(n->user.setUserName(n));
        Optional.ofNullable(email)
                .filter(n -> !n.equals(user.getEmail()))
                .ifPresent(e -> user.setEmail(e));
        Optional.ofNullable(password)
                .filter(n -> !n.equals(user.getPassword()))
                .ifPresent(p -> user.setPassword(p));

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
