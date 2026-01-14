package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
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
    public User findById(UUID id){
        if(data.get(id) == null){
            throw new IllegalArgumentException("해당 id의 유저가 없습니다.");
        }
        return data.get(id);
    }

    @Override
    public List<User> findAll(){
        return new ArrayList<>(data.values());
    }

    @Override
    public User update(UUID id,String userName,String email,String status){
        User user = findById(id);
        user.setUserName(userName);
        user.setEmail(email);
        user.setStatus(status);
        return user;
    }

    @Override
    public User delete(UUID id){
        User user = findById(id);
        data.remove(id);
        return user;
    }

    public List<Channel> selectChannel(UUID id){
        User user = findById(id);
        return user.getChannelList();
    }

    public List<Message> selectMessage(UUID id){
        User user = findById(id);
        return user.getMessageList();
    }


}
