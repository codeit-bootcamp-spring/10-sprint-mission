package com.sprint.mission.descodeit.service.jcf;

import com.sprint.mission.descodeit.entity.Channel;
import com.sprint.mission.descodeit.entity.Message;
import com.sprint.mission.descodeit.entity.User;
import com.sprint.mission.descodeit.service.ChannelService;
import com.sprint.mission.descodeit.service.UserService;

import java.util.*;

public class JCFUserService implements UserService {
    private final Map<UUID, User> data;
    private ChannelService channelService;

    public JCFUserService(){
        this.data = new HashMap<>();
        this.channelService = new JCFChannelService();
    }

    @Override
    public void create(User user) {
        data.put(user.getId(), user);
    }

    @Override
    public User findUser(UUID userID) {
        System.out.println(data.get(userID));
        return data.get(userID);
    }

    @Override
    public Set<UUID> findAllUsers() {
        System.out.println("[유저 전체 조회]");
        for(UUID id : data.keySet()){
            System.out.println(data.get(id).getName());
        }
        return data.keySet();
    }

    @Override
    public void update(UUID userID,String newName) {
        if(data.get(userID) != null) {
            data.get(userID).updateUser(newName);
        }
        else{
            System.out.println("해당 유저가 없습니다.");
        }
    }

    @Override
    public void delete(UUID userID) {
        User user = data.get(userID);
        List<Channel> channelList = user.getChannelList();
        for(Channel channel : channelList){
            channel.getUserList().remove(user);
        }

        data.remove(userID);
    }



}
