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
    private static final String CHANNEL_FILE = "data/channel.ser";
    private final UserService userService;
    private final MessageService messageService;

    public FileChannelService(UserService userService, MessageService messageService){
        this.userService = userService;
        this.messageService = messageService;
    }

    @Override
    public Channel create(String name) {
        // 파일 불러오기
        Map<UUID,Channel> data = loadChannel();

        Channel channel = new Channel(name);
        data.put(channel.getId(), channel);

        // 파일 저장하기
        saveChannel(data);
        return channel;
    }

    @Override
    public Channel joinUsers(UUID channelId, UUID... userId) {
        // 파일 불러오기
        Map<UUID,Channel> channelData = loadChannel();
        Channel channel = channelData.get(channelId);


        // 유저 추가
        Arrays.stream(userId)
                .map(userService::findUser)
                .forEach(user -> {
                    channel.addUsers(user.getId());
                    user.addChannel(channelId);
                    userService.save(user);
                });
        // 파일 저장하기
        saveChannel(channelData);
        return channel;
    }

    @Override
    public Channel findChannel(UUID channelId) {
        // 파일 불러오기
        Map<UUID,Channel> data = loadChannel();
        Channel channel = Optional.ofNullable(data.get(channelId))
                .orElseThrow(()->new NoSuchElementException("해당 채널을 찾을 수 없습니다"));

        return channel;
    }

    @Override
    public List<Channel> findAllChannel() {
        // 파일 불러오기
        Map<UUID,Channel> data = loadChannel();
        System.out.println("[채널 전체 조회]");
        data.keySet().forEach(uuid -> System.out.println(data.get(uuid)));


        return new ArrayList<>(data.values());
    }

    @Override
    public List<Channel> findAllChannelsByUserId(UUID userId) {
        // 파일 불러오기
        Map<UUID,Channel> data = loadChannel();
        User user = userService.findUser(userId);

        System.out.println("-- " + user + "가 속한 채널 조회 --");
        List<Channel> channelList = user.getChannelList().stream()
                .map(this::findChannel).filter(Objects::nonNull).toList();

        channelList.forEach(System.out::println);

        return channelList;
    }

    @Override
    public Channel update(UUID channelId, String newName) {
        // 파일 불러오기
        Map<UUID,Channel> data = loadChannel();
        Channel channel = data.get(channelId);

        channel.updateChannel(newName);

        // 파일 저장하기
        saveChannel(data);
        return channel;
    }

    @Override
    public void delete(UUID channelId) {
        // 파일 불러오기
        Map<UUID,Channel> data = loadChannel();
        Channel channel = data.get(channelId);

        // 채널이 삭제될때 이 채널이 속해있는 유저의 채널리스트에서 채널 삭제
        List<UUID> userList = new ArrayList<>(channel.getUserList());
        userList.forEach(userId -> userService.findUser(userId).getChannelList().remove(channelId));

        // 채널이 삭제될때 채널에 속해있던 메시지들 전부 삭제
        List<UUID> messageList = new ArrayList<>(channel.getMessageList());
        messageList.forEach(messageService::delete);

        data.remove(channelId);

        // 파일 저장하기
        saveChannel(data);
    }

    public void save(Channel channel){
        Map<UUID, Channel> data = loadChannel();
        data.put(channel.getId(), channel);
        saveChannel(data);
    }

    private Map<UUID, Channel> loadChannel(){
        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(CHANNEL_FILE))){
            return (Map<UUID,Channel>) ois.readObject();
        }
        catch(Exception e){
            return new HashMap<>();
        }
    }

    private void saveChannel(Map<UUID, Channel> data) {
        try(ObjectOutputStream oos = new ObjectOutputStream((new FileOutputStream(CHANNEL_FILE)))){
            oos.writeObject(data);
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
