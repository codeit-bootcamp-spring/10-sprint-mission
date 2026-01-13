package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFUserService implements UserService {

    private final Map<UUID, User> data;

    public JCFUserService(){
        this.data = new HashMap<>();
    }

    @Override
    public User create(String userName,String email, String status){
        User user = new User(userName,email,status);
        data.put(user.getId(),user);

        return user;
    }

    @Override
    public User findById(User user){
        if(data.get(user.getId()) == null){
            throw new IllegalArgumentException("해당 id의 유저가 없습니다.");
        }
        return data.get(user.getId());
    }

    @Override
    public List<User> findAll(){
        return new ArrayList<>(data.values());
    }

    @Override
    public User update(User user,String userName,String email,String status){
        User users = data.get(user.getId());
        if(users == null){
            throw new IllegalArgumentException("수정할 사용자가 없습니다.");
        }
        users.setUserName(userName);
        users.setEmail(email);
        users.setStatus(status);
        return users;
    }

    @Override
    public void delete(User user){
        if(data.get(user.getId()) == null){
            throw new IllegalArgumentException("해당 id의 유저가 없습니다.");
        }
        data.remove(user.getId());
    }

}
