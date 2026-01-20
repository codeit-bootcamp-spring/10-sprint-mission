package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.io.*;
import java.util.*;

public class FileChannelService implements ChannelService {
    private static final String FILE_PATH = "channels.ser";
    private final MessageService messageService;
    private final UserService userService;
    private Map<UUID, Channel> data;

    public FileChannelService(MessageService messageService, UserService userService) {
        this.messageService = messageService;
        this.userService = userService;
        this.data = loadChannels();
    }

    @Override
    public Channel createChannel(String channelName) {
        Channel channel = new Channel(channelName);
        data.put(channel.getId(), channel);
        saveChannels();

        return channel;
    }

    @Override
    public List<Channel> getChannelList() {
        return data.values().stream().toList();
    }

    @Override
    public List<Channel> getChannelsByUser(UUID userId) {
        // 유저가 존재하지 않을 경우 예외 처리
        if (userService.getUserInfoByUserId(userId) == null) {
            throw new NoSuchElementException("해당 id를 가진 유저가 존재하지 않습니다.");
        }

        return data.values().stream()
                .filter(channel ->
                        channel.getJoinedUsers().stream()
                                .anyMatch(user -> user.getId().equals(userId)))
                .toList();
    }

    @Override
    public Channel getChannelInfoById(UUID channelId) {
        return findChannelById(channelId);
    }

    @Override
    public Channel updateChannelName(UUID channelId, String newChannelName) {
        Objects.requireNonNull(channelId, "channelId는 null일 수 없습니다.");

        Channel channel = findChannelById(channelId);
        if (channel.getChannelName().equals(newChannelName)) {
            throw new IllegalArgumentException("해당 채널의 이름이 바꿀 이름과 동일합니다.");
        }
        channel.updateChannelName(newChannelName);
        saveChannels();
        // 변경된 객체 반환
        return channel;
    }

    @Override
    public void joinChannel(UUID channelId, UUID userID) {
        Objects.requireNonNull(userID, "userId값은 null일 수 없습니다.");

        Channel channel = findChannelById(channelId);
        User user = userService.getUserInfoByUserId(userID);

        channel.addUser(user);
        user.updateJoinedChannels(channel);
        saveChannels();
    }

    @Override
    public void leaveChannel(UUID channelId, UUID userID) {
        Objects.requireNonNull(userID, "userId값은 null일 수 없습니다.");

        Channel channel = findChannelById(channelId);
        User user = userService.getUserInfoByUserId(userID);

        channel.removeUser(user.getId());
        user.removeChannel(channel);
        saveChannels();
    }

    @Override
    public void deleteChannel(UUID channelId) {
        Channel channel = findChannelById(channelId);

        messageService.clearChannelMessage(channelId);
        data.remove(channelId);
        saveChannels();
    }

    private Channel findChannelById(UUID channelId) {
        Objects.requireNonNull(channelId, "channelId는 null일 수 없습니다.");

        Channel channel = data.get(channelId);

        if (channel == null) {
            throw new NoSuchElementException("해당 id를 가진 채널이 존재하지 않습니다.");
        }

        return channel;
    }

    @SuppressWarnings("unchecked")
    private Map<UUID, Channel> loadChannels() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            return new HashMap<>();
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (Map<UUID, Channel>) ois.readObject();
        } catch (Exception e) {
            throw new RuntimeException("채널 데이터 로드 실패", e);
        }
    }

    private void saveChannels() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(data);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
