package com.sprint.mission.descodeit.service.file;

import com.sprint.mission.descodeit.entity.Channel;
import com.sprint.mission.descodeit.entity.Message;
import com.sprint.mission.descodeit.entity.User;
import com.sprint.mission.descodeit.service.MessageService;
import com.sprint.mission.descodeit.service.UserService;

import java.io.*;
import java.util.*;

public class FileUserService implements UserService {
    private Map<UUID, User> data;
    private static final String USER_FILE = "data/user.ser";
    private final MessageService messageService;

    public FileUserService(MessageService messageService){
        data = loadUser();
        this.messageService = messageService;
        this.data = loadUser();

    }

    @Override
    public User create(String name) {
        data = loadUser();
        User user = new User(name);
        data.put(user.getId(),user);

        saveUser();
        return user;
    }

    @Override
    public User findUser(UUID userId) {
        data = loadUser();
        User user = Optional.ofNullable(data.get(userId))
                .orElseThrow(() -> new NoSuchElementException("해당 사용자를 찾을 수 없습니다"));
        saveUser();
        return user;
    }

    @Override
    public List<User> findAllUsers() {
        data = loadUser();
        System.out.println("[유저 전체 조회]");
        data.keySet().forEach(uuid -> System.out.println(data.get(uuid)));

        saveUser();
        return new ArrayList<>(data.values());
    }

    @Override
    public List<User> findFriends(UUID userId) {
        User user = findUser(userId);
        List<User> friendsList = user.getFriendsList();

        System.out.println(" -- 친구 목록 조회 --");
        if(friendsList.isEmpty()){
            System.out.println("친구가 없습니다");
        }
        else{
            friendsList.forEach(System.out::println);
        }

        saveUser();
        return friendsList;
    }

    @Override
    public User addFriend(UUID senderId, UUID receiverId) {
        User sender = findUser(senderId);
        User receiver = findUser(receiverId);
        sender.addFriend(receiver);

        saveUser();
        return receiver;
    }

    @Override
    public User update(UUID userId,String newName) {
        User user = findUser(userId);
        user.updateUser(newName);
        saveUser();
        return user;
    }

    @Override
    public void delete(UUID userId) {
        User user = findUser(userId);

        List<Channel> channelList = new ArrayList<>(user.getChannelList());
        // 유저 삭제시 유저가 속한 채널의 유저 리스트에서 삭제
        channelList.forEach(channel -> channel.getUserList().remove(user));

        List<Message> messageList = new ArrayList<>(user.getMessageList());
        // 유저가 가지고 있던 메시지 삭제
        messageList.forEach(message -> messageService.delete(message.getId()));


        List<User> friendsList = new ArrayList<>(user.getFriendsList());
        // 유저의 친구들의 친구목록에서 유저 삭제
        friendsList.forEach(friend -> friend.getFriendsList().remove(user));

        data.remove(userId);
        saveUser();
    }

    public Map<UUID, User> loadUser(){
        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(USER_FILE))){
            return (Map<UUID,User>) ois.readObject();
        }
        catch(Exception e){
            return new HashMap<>();
        }
    }

    public void saveUser(){
        try(ObjectOutputStream oos = new ObjectOutputStream((new FileOutputStream(USER_FILE)))){
            oos.writeObject(this.data);
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
