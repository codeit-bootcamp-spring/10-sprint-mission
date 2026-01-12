package com.sprint.mission.descodeit.service.jcf;

import com.sprint.mission.descodeit.entity.Channel;
import com.sprint.mission.descodeit.entity.Message;
import com.sprint.mission.descodeit.entity.User;
import com.sprint.mission.descodeit.service.ChannelService;
import com.sprint.mission.descodeit.service.MessageService;
import com.sprint.mission.descodeit.service.UserService;

import java.util.*;

public class JCFUserService implements UserService {
    private final Map<UUID, User> data;

    public JCFUserService(){
        this.data = new HashMap<>();
    }

    @Override
    public User create(String name) {
        User user = new User(name);
        data.put(user.getId(), user);
        return user;
    }

    @Override
    public User findUser(UUID userID) {
        User user = data.get(userID);
        if(user == null){
            throw new NoSuchElementException();
        }
        System.out.println(user);
        return user;
    }

    @Override
    public List<User> findAllUsers() {
        System.out.println("[유저 전체 조회]");
        if(data.isEmpty()){
            System.out.println("조회할 유저가 없습니다");
            return new ArrayList<>();
        }

        for(UUID id : data.keySet()){
            System.out.println(data.get(id).getName());
        }
        return new ArrayList<>(data.values());
    }

    @Override
    public User update(UUID userID,String newName) {
        User user = data.get(userID);
        if(user == null){
            throw new NoSuchElementException();
        }
        user.updateUser(newName);
        return user;
    }

    @Override
    public boolean delete(UUID userID) {
        User user = data.get(userID);
        if(user == null){
            throw new NoSuchElementException();
        }

        // 유저 삭제시 유저가 속한 채널의 유저 리스트에서 삭제
        List<Channel> channelList = user.getChannelList();
        for(Channel channel : channelList){
            channel.getUserList().remove(user);
        }

        // 유저가 가지고 있던 메시지 삭제
        MessageService messageService = new JCFMessageService();
        for(Message message : user.getMessageList()){
            messageService.delete(message.getId());
        }

        data.remove(userID);
        return true;
    }



}
