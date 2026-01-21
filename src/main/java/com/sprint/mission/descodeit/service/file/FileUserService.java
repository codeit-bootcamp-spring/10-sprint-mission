package com.sprint.mission.descodeit.service.file;

import com.sprint.mission.descodeit.entity.Channel;
import com.sprint.mission.descodeit.entity.Message;
import com.sprint.mission.descodeit.entity.User;
import com.sprint.mission.descodeit.service.ChannelService;
import com.sprint.mission.descodeit.service.MessageService;
import com.sprint.mission.descodeit.service.UserService;

import java.io.*;
import java.util.*;

public class FileUserService implements UserService {
    private static final String USER_FILE = "data/user.ser";
    private final MessageService messageService;
    private final ChannelService channelService;

    public FileUserService(MessageService messageService, ChannelService channelService){
        this.messageService = messageService;
        this.channelService = channelService;
    }


    @Override
    public User create(String name) {
        Map<UUID, User> data = loadUser();

        User user = new User(name);
        data.put(user.getId(),user);

        saveUser(data);
        return user;
    }

    @Override
    public User findUser(UUID userId) {
        Map<UUID, User> data = loadUser();
        User user = Optional.ofNullable(data.get(userId))
                .orElseThrow(() -> new NoSuchElementException("해당 사용자를 찾을 수 없습니다"));

        return user;
    }

    @Override
    public List<User> findAllUsers() {
        Map<UUID, User> data = loadUser();
        System.out.println("[유저 전체 조회]");
        data.keySet().forEach(uuid -> System.out.println(data.get(uuid)));

        return new ArrayList<>(data.values());
    }

    @Override
    public List<User> findFriends(UUID userId) {

        User user = findUser(userId);
        List<UUID> friendsList = user.getFriendsList();

        System.out.println(" -- 친구 목록 조회 --");
        if(friendsList.isEmpty()){
            System.out.println("친구가 없습니다");
        }
        else{
            friendsList.forEach(System.out::println);
        }

        return friendsList.stream().map(this::findUser).toList();
    }

    @Override
    public User addFriend(UUID senderId, UUID receiverId) {
        Map<UUID, User> data = loadUser();
        User sender = data.get(senderId);

        User receiver = data.get(receiverId);
        sender.addFriend(receiverId);
        receiver.addFriend(senderId);

        data.put(senderId, sender);
        data.put(receiverId, receiver);

        return receiver;
    }

    @Override
    public User update(UUID userId,String newName) {

        Map<UUID, User> data = loadUser();
        User user = data.get(userId);
        user.updateUser(newName);
        data.put(userId, user);

        saveUser(data);
        return user;
    }

    @Override
    public void delete(UUID userId) {
        Map<UUID, User> data = loadUser();
        User user = data.get(userId);

        List<UUID> channelList = new ArrayList<>(user.getChannelList());
        // 유저 삭제시 유저가 속한 채널의 유저 리스트에서 삭제
        channelList.stream().map(channelService::findChannel)
                .forEach(channel -> channel.getUserList().remove(userId));

        List<UUID> messageList = new ArrayList<>(user.getMessageList());
        // 유저가 가지고 있던 메시지 삭제
        messageList.forEach(message -> messageService.delete(userId));


        List<UUID> friendsList = new ArrayList<>(user.getFriendsList());
        // 유저의 친구들의 친구목록에서 유저 삭제
        friendsList.forEach(friendId -> this.findUser(friendId).getFriendsList().remove(userId));

        data.remove(userId);
        saveUser(data);
    }

    @Override
    public void save(User user) {
        Map<UUID, User> data = loadUser();
        data.put(user.getId(),user);
        saveUser(data);
    }

    private Map<UUID, User> loadUser(){
        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(USER_FILE))){
            return (Map<UUID,User>) ois.readObject();
        }
        catch(Exception e){
            return new HashMap<>();
        }
    }

    private void saveUser(Map<UUID, User> data){
        try(ObjectOutputStream oos = new ObjectOutputStream((new FileOutputStream(USER_FILE)))){

            oos.writeObject(data);
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
