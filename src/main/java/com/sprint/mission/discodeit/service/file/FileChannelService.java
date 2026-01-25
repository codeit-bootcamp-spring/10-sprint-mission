package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.*;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class FileChannelService implements ChannelService {
    private static final String FILE_PATH = "channels.dat";
    private final UserService userService;

    public FileChannelService(UserService userService) {
        this.userService = userService;
    }

    private Map<UUID, Channel> load(){
        File file = new File(FILE_PATH);
        if (!file.exists()){
            return new LinkedHashMap<>();
        }

        try (ObjectInputStream ois =
                     new ObjectInputStream(new FileInputStream(file))){
            return (Map<UUID, Channel>) ois.readObject();
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    private void save(Map<UUID, Channel> channels){
        try (ObjectOutputStream oos =
                     new ObjectOutputStream(new FileOutputStream(FILE_PATH))){
            oos.writeObject(channels);
        } catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    // 파일 초기화
    public void resetChannelFile() {
        try {
            File file = new File(FILE_PATH);
            if (file.exists()) file.delete();
            save(new LinkedHashMap<>());
        } catch (Exception e) {
            throw new RuntimeException("Channel 파일 초기화 실패", e);
        }
    }

    @Override
    public Channel createChannel(String channelName) {
        Map<UUID, Channel> channels = load();

        if (channels.values().stream()
                .anyMatch(c -> c.getChannelName().equals(channelName))){
            throw new DuplicationChannelException();
        }

        Channel channel = new Channel(channelName);
        channels.put(channel.getId(), channel);
        save(channels);
        return channel;
    }


    @Override
    public Channel findChannel(UUID channelId) {
        Map<UUID, Channel> channels = load();
        Channel channel = channels.get(channelId);
        if (channel == null) {
            throw new ChannelNotFoundException();
        }
        return channel;
    }

    @Override
    public List<Channel> findAllChannel() {
        Map<UUID, Channel> channels = load();

        return new ArrayList<>(channels.values());
    }

    @Override
    public Channel userAddChannel(UUID channelId, UUID userId) {
        Map<UUID, Channel> channels = load();

        Channel channel = channels.get(channelId);
        if (channel == null) {
            throw new ChannelNotFoundException();
        }

        User user = userService.findUser(userId);
        if (channel.hasChannelUser(user)) {
            throw new AlreadyJoinedChannelException();
        }

        channel.addChannelUser(user);
        save(channels);

        return channel;
    }

    @Override
    public Channel nameUpdateChannel(UUID channelId, String channelName) {
        Map<UUID, Channel> channels = load();

        Channel channel = channels.get(channelId);
        if (channel == null){
            throw new ChannelNotFoundException();
        }
        if(channels.values().stream()
                .anyMatch(c -> c.getChannelName().equals(channelName))){
            throw new DuplicationChannelException();
        }

        channel.updateChannelName(channelName);

        save(channels);
        return channel;
    }

    @Override
    public Channel deleteChannel(UUID channelId) {
        Map<UUID, Channel> channels = load();

        Channel channel = channels.get(channelId);
        if (channel == null){
            throw new ChannelNotFoundException();
        }
        channels.remove(channelId);

        save(channels);
        return channel;
    }

    //특정 사용자의 이용채널 조회
    @Override
    public Channel findByUserChannel(UUID userId) {
        Map<UUID, Channel> channels = load();

        userService.findUser(userId);

        return channels.values().stream()
                .filter(channel ->
                        channel.getChannelUser().stream()
                                .anyMatch(user -> user.getId().equals(userId))
                )
                .findFirst()
                .orElseThrow(ChannelNotFoundException::new);
    }

    // 특정채널 전체 사용자 조회
    public String findAllUserInChannel(UUID channelId) {
        Map<UUID, Channel> channels = load();

        Channel channel = findChannel(channelId);
        if (channel.getChannelUser().isEmpty()) {
            throw new UserNotInChannelException();
        }

        return channel.getChannelUser().stream()
                .map(User::getUserName)
                .collect(Collectors.joining(", "));
    }


}