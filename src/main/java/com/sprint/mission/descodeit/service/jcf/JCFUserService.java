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
    public List<User> findFriends(UUID userId) {
        User user = findUser(userId);
        List<User> friendsList = user.getFriendsList();

        System.out.println(" -- 친구 목록 조회 --");
        for(User friend : friendsList){
            System.out.println(friend);
        }
        System.out.println();
        return friendsList;
    }

    @Override
    public User addFriend(UUID senderId, UUID receiverId) {
        User sender = findUser(senderId);
        User receiver = findUser(receiverId);
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

        List<Channel> channelList = new ArrayList<>(user.getChannelList());
        // 유저 삭제시 유저가 속한 채널의 유저 리스트에서 삭제
        for(Channel channel : channelList){
            channel.getUserList().remove(user);
        }

        List<Message> messageList = new ArrayList<>(user.getMessageList());
        // 유저가 가지고 있던 메시지 삭제
        for(Message message : messageList){
            messageService.delete(message.getId());
        }

        List<User> friendsList = new ArrayList<>(user.getFriendsList());
        // 유저의 친구들의 친구목록에서 유저 삭제
        for(User friend : friendsList){
            friend.getFriendsList().remove(user);
        }

        data.remove(userId);
    }



}
