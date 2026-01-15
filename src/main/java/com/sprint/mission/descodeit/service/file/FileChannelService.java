package com.sprint.mission.descodeit.service.file;

import com.sprint.mission.descodeit.entity.Channel;
import com.sprint.mission.descodeit.entity.Message;
import com.sprint.mission.descodeit.entity.User;
import com.sprint.mission.descodeit.service.ChannelService;
import com.sprint.mission.descodeit.service.MessageService;
import com.sprint.mission.descodeit.service.UserService;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.*;

public class FileChannelService implements ChannelService {
    private Map<UUID, Channel> data;
    private static final String CHANNEL_FILE = "data/channel.ser";
    private final UserService userService;
    private final MessageService messageService;

    public FileChannelService(UserService userService, MessageService messageService){
        this.data = loadChannel();
        this.userService = userService;
        this.messageService = messageService;
    }

    @Override
    public Channel create(String name) {
        data = loadChannel();
        Channel channel = new Channel(name);
        data.put(channel.getId(), channel);

        saveChannel();
        return channel;
    }

    @Override
    public Channel joinUsers(UUID channelId, UUID... userId) {
        Channel channel = findChannel(channelId);

        // 유저 추가
        Arrays.stream(userId)
                .map(userService::findUser)
                .forEach(channel::addUsers);

        saveChannel();
        return channel;
    }

    @Override
    public Channel findChannel(UUID channelId) {
        data = loadChannel();
        Channel channel = Optional.ofNullable(data.get(channelId))
                .orElseThrow(()->new NoSuchElementException("해당 채널을 찾을 수 없습니다"));
        saveChannel();
        return channel;
    }

    @Override
    public List<Channel> findAllChannel() {
        data = loadChannel();
        System.out.println("[채널 전체 조회]");
        data.keySet().forEach(uuid -> System.out.println(data.get(uuid)));

        saveChannel();
        return new ArrayList<>(data.values());
    }

    @Override
    public List<Channel> findAllChannelsByUserId(UUID userId) {
        User user = userService.findUser(userId);
        List<Channel> channelList = new ArrayList<>();

        System.out.println("-- " + user + "가 속한 채널 조회 --");
        Collection<Channel> dataList = new ArrayList<>(data.values());
        dataList.stream()
                .filter(channel -> channel.getUserList().contains(user))
                .forEach(channel -> {channelList.add(channel);
                    System.out.println(channel);});

        saveChannel();
        return channelList;
    }

    @Override
    public Channel update(UUID channelId, String newName) {
        Channel channel = findChannel(channelId);
        channel.updateChannel(newName);
        saveChannel();
        return channel;
    }

    @Override
    public void delete(UUID channelId) {
        Channel channel = findChannel(channelId);

        // 채널이 삭제될때 이 채널이 속해있는 유저의 채널리스트에서 채널 삭제
        List<User> userList = new ArrayList<>(channel.getUserList());
        userList.forEach(user -> user.getChannelList().remove(channel));

        // 채널이 삭제될때 채널에 속해있던 메시지들 전부 삭제
        List<Message> messageList = new ArrayList<>(channel.getMessageList());
        messageList.forEach(message -> messageService.delete(message.getId()));

        data.remove(channelId);
        saveChannel();
    }

    public Map<UUID, Channel> loadChannel(){
        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(CHANNEL_FILE))){
            return (Map<UUID,Channel>) ois.readObject();
        }
        catch(Exception e){
            return new HashMap<>();
        }
    }

    public void saveChannel(){
        try(ObjectOutputStream oos = new ObjectOutputStream((new FileOutputStream(CHANNEL_FILE)))){
            oos.writeObject(this.data);
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
