package com.sprint.mission.descodeit.service.jcf;

import com.sprint.mission.descodeit.entity.Channel;
import com.sprint.mission.descodeit.entity.Message;
import com.sprint.mission.descodeit.entity.User;

import com.sprint.mission.descodeit.service.MessageService;
import com.sprint.mission.descodeit.service.UserService;

import java.util.*;

public class JCFUserService implements UserService {
    private final Map<UUID, User> data;
    private final MessageService messageService;

    public JCFUserService(MessageService messageService){
        this.data = new HashMap<>();
        this.messageService = messageService;
    }

    @Override
    public User create(String name) {
        User user = new User(name);
        data.put(user.getId(), user);
        return user;
    }

    @Override
    public User findUser(UUID userId) {
        User user = Optional.ofNullable(data.get(userId))
                .orElseThrow(() -> new NoSuchElementException("해당 사용자를 찾을 수 없습니다"));
        return user;
    }

    @Override
    public List<User> findAllUsers() {
        System.out.println("[유저 전체 조회]");
        for(UUID id : data.keySet()){
            System.out.println(data.get(id).getName());
        }
        System.out.println();
        return new ArrayList<>(data.values());
    }

    @Override
    public User addFriend(UUID senderId, UUID receiverId) {
        User sender = data.get(senderId);
        User receiver = data.get(receiverId);
        sender.addFriend(receiver);

        return receiver;
    }

    @Override
    public User update(UUID userId,String newName) {
        User user = findUser(userId);
        user.updateUser(newName);
        return user;
    }

    @Override
    public void delete(UUID userId) {
        User user = findUser(userId);

        // 유저 삭제시 유저가 속한 채널의 유저 리스트에서 삭제
        List<Channel> channelList = user.getChannelList();
        for(Channel channel : channelList){
            channel.getUserList().remove(user);
        }

        // 유저가 가지고 있던 메시지 삭제
        for(Message message : new ArrayList<>(user.getMessageList())){
            messageService.delete(message.getId());
        }

        data.remove(userId);
    }



}
