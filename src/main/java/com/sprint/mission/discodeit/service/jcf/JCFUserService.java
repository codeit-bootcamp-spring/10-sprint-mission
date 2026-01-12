package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JCFUserService implements UserService {
    private final List<User> userData;

    public JCFUserService() {
        this.userData = new ArrayList<>();
    }

    // User 등록
    @Override
    public User create(String name) {
        User user = new User(name);
        this.userData.add(user);
        return user;
    }

    // 단건 조회
    @Override
    public User read(UUID id){
        return userData.stream()
                .filter(user -> user.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    // 다건 조회
    @Override
    public List<User> readAll(){
        return userData;
    }

    // User 수정
    @Override
    public void update(UUID id, String name){
        User user = read(id);
        user.updateName(name);
    }

    // User 삭제
    @Override
    public void delete(UUID id) {
        User delUser = read(id);
        userData.remove(delUser);
    }

}
