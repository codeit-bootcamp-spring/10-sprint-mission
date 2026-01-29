package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.io.*;
import java.util.*;

public class FileChannelService implements ChannelService {
    private final String FILE_PATH = "channels.dat";
    private final UserService userService;
    private MessageService messageService;

    public FileChannelService(UserService userService) {
        this.userService = userService;
    }

    public void setMessageService(MessageService messageService) {
        this.messageService = messageService;
    }

    private Map<UUID, Channel> loadData() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return new HashMap<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (Map<UUID, Channel>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new HashMap<>();
        }
    }

    private void saveData(Map<UUID, Channel> data) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Channel createChannel(String name, String type) {
        validateChannelInput(name, type);
        Map<UUID, Channel> data = loadData();
        Channel channel = new Channel(name, type);
        data.put(channel.getId(), channel);
        saveData(data);
        return channel;
    }

    @Override
    public Channel getChannel(UUID id) {
        return validateChannelId(id);
    }

    @Override
    public List<Channel> getAllChannels() {
        return new ArrayList<>(loadData().values());
    }

    @Override
    public Channel updateChannel(UUID id, String name, String type) {
        Map<UUID, Channel> data = loadData();
        Channel channel = validateChannelId(id, data);
        Optional.ofNullable(name).filter(n -> !n.isBlank()).ifPresent(channel::updateName);
        Optional.ofNullable(type).filter(t -> !t.isBlank()).ifPresent(channel::updateType);
        saveData(data);
        return channel;
    }

    @Override
    public void deleteChannel(UUID id) {
        Map<UUID, Channel> data = loadData();
        Channel channel = validateChannelId(id, data);
        if (messageService != null) {
            new ArrayList<>(channel.getMessages()).forEach(m -> messageService.deleteMessage(m.getId()));
        }
        channel.getUsers().forEach(user -> user.removeChannel(channel));
        data.remove(id);
        saveData(data);
    }

    @Override
    public void enterChannel(UUID userId, UUID channelId) {
        User user = userService.getUser(userId);
        Map<UUID, Channel> data = loadData();
        Channel channel = validateChannelId(channelId, data);
        if (channel.getUsers().contains(user)) throw new IllegalArgumentException("이미 해당 채널에 참가 중입니다.");
        channel.addUser(user);
        user.addChannel(channel);
        saveData(data);
    }

    @Override
    public void leaveChannel(UUID userId, UUID channelId) {
        User user = userService.getUser(userId);
        Map<UUID, Channel> data = loadData();
        Channel channel = validateChannelId(channelId, data);
        if (!channel.getUsers().contains(user)) throw new IllegalArgumentException("참가하고 있지 않은 채널입니다.");
        if (messageService != null) {
            List<Message> targetMessages = channel.getMessages().stream()
                    .filter(m -> m.getAuthorId().equals(user.getId())).toList();
            targetMessages.forEach(m -> messageService.deleteMessage(m.getId()));
        }
        channel.removeUser(user);
        user.removeChannel(channel);
        saveData(data);
    }

    @Override
    public List<Channel> getChannelsByUserId(UUID userId) {
        return loadData().values().stream()
                .filter(c -> c.getUsers().stream().anyMatch(user -> user.getId().equals(userId)))
                .toList();
    }

    @Override
    public List<User> getUsersByChannelId(UUID channelId) {
        return new ArrayList<>(validateChannelId(channelId).getUsers());
    }

    private void validateChannelInput(String name, String type) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("채널 이름은 필수입니다.");
        if (type == null || type.isBlank()) throw new IllegalArgumentException("채널 타입은 필수입니다.");
    }

    private Channel validateChannelId(UUID id) {
        return validateChannelId(id, loadData());
    }

    private Channel validateChannelId(UUID id, Map<UUID, Channel> data) {
        return Optional.ofNullable(data.get(id))
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채널입니다"));
    }
}
