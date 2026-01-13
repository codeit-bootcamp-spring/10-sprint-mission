package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class JCFUserService implements UserService {
    // field
    private final List<User> userData;

    // constructor
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
    public User find(UUID id){
        return userData.stream()
                .filter(user -> user.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));
    }

    // 다건 조회
    @Override
    public List<User> readAll(){
        return userData;
    }

    // User 수정
    @Override
    public User update(UUID id, String name){
        User user = find(id);
        user.updateName(name);
        return user;
    }

    // User 삭제
    @Override
    public void delete(UUID id) {
        User delUser = find(id);
        userData.remove(delUser);
    }

    // User가 보낸 메시지 출력
    public List<String> getMessages(UUID userID){
        User user = find(userID);
        return user.getMessageList().stream()
                .map(Message::getContents)
                .collect(Collectors.toList());
    }

}
