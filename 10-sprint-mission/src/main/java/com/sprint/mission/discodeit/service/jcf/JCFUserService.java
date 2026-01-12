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
    public User create(User user){
        data.put(user.getId(),user);

        return user;
    }

    @Override
    public User findById(UUID id){
        return data.get(id);
    }

    @Override
    public List<User> findAll(){
        return new ArrayList<>(data.values());
    }

    @Override
    public User update(UUID id,String userName,String email,String status){
        User user = data.get(id);
        if(user != null){
            user.setUserName(userName);
            user.setEmail(email);
            user.setStatus(status);
        }
        return user;
    }

    @Override
    public void delete(UUID id){
        data.remove(id);
    }

}
