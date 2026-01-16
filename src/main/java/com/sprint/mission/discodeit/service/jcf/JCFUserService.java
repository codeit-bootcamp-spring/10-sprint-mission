package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFUserService implements UserService {
    private final Map<UUID, User> data = new HashMap<>();

    // 유저 생성
    @Override
    public User create(String name, String nickname, String email, String password){
        User newUser = new User(name, nickname, email, password);
        data.put(newUser.getId(), newUser);
        return newUser;
    }

    // 유저 ID로 조회
    @Override
    public User findById(UUID id){
        User user = data.get(id);
        if (user == null){
            throw new NoSuchElementException("실패: 존재하지 않는 유저 ID입니다.");
        }
        return user;
    }

    // 유저 전부 조회
    @Override
    public List<User> findAll(){
        return new ArrayList<>(data.values());
    }

    // 유저 수정
    @Override
    public User update(UUID id, String name, String nickname, String email){
        User user = findById(id);
        user.update(name, nickname, email);
        return user;
    }

    // 유저 삭제
    @Override
    public void delete(UUID id){
        findById(id);
        data.remove(id);
    }
}
