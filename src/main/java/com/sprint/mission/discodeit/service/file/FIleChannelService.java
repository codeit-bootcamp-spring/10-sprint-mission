package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FIleChannelService implements ChannelService {

    private final Path filePath;
    private final List<Channel> data;
    private final UserService userService;

    public FIleChannelService(UserService userService, String path) {
        this.filePath = Paths.get(path, "channels.ser");
        this.userService = userService;
        init(filePath.getParent());
        this.data = load();
    }

    @Override
    public Channel create(ChannelType type, String name, String description) {
        existsByChannelName(name);
        Channel channel = new Channel(type, name, description);
        data.add(channel);
        save();
        return channel;
    }

    @Override
    public Channel findChannelById(UUID channelId) {
        return data.stream()
                .filter(channel -> channel.getId().equals(channelId))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채널입니다."));
    }

    @Override
    public Channel findChannelByName(String name) {
        return data.stream()
                .filter(channel -> channel.getChannelName().equals(name))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채널입니다."));
    }

    @Override
    public List<Channel> findAllChannel() {
        return new ArrayList<>(data);
    }

    //특정 사용자의 참가한 채널 리스트 조회
    @Override
    public List<Channel> findChannelsByUser(UUID userId) {
        return userService.findUserById(userId).getChannels();
    }

    @Override
    public Channel update(UUID channelId, String name) {
        Channel channel = findChannelById(channelId);
        existsByChannelName(name);
        channel.update(name);
        save();
        return channel;
    }

    @Override
    public void delete(UUID channelId) {
        Channel channel = findChannelById(channelId);

        channel.getMessages().forEach(message -> {
            message.getUser().delete(message);
            channel.delete(message);
        });
        channel.getUsers().forEach(user -> {
            user.leave(channel);
            channel.leave(user);
        });
        save();
        data.remove(channel);
    }

    @Override
    public void join(UUID channelId, UUID userId) {
        Channel channel = findChannelById(channelId);
        User user = userService.findUserById(userId);
        channel.join(user);
        user.join(channel);
    }

    @Override
    public void leave(UUID channelId, UUID userId) {
        Channel channel = findChannelById(channelId);
        User user = userService.findUserById(userId);
        user.leave(channel);
        channel.leave(user);
    }

    //채널명 중복체크
    private void existsByChannelName(String name) {
        boolean exist = data.stream().anyMatch(channel -> channel.getChannelName().equals(name));
        if (exist) {
            throw new IllegalArgumentException("이미 사용중인 채널명입니다: " + name);
        }
    }

    private void init(Path directory) {
        if (!Files.exists(directory)) {
            try {
                Files.createDirectories(directory);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void save() {
        try (
                FileOutputStream fos = new FileOutputStream(filePath.toFile());
                ObjectOutputStream oos = new ObjectOutputStream(fos)
        ) {
            oos.writeObject(data);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private List<Channel> load() {
        if (!Files.exists(filePath)) {
            return new ArrayList<>();
        }

        try (
                FileInputStream fis = new FileInputStream(filePath.toFile());
                ObjectInputStream ois = new ObjectInputStream(fis)
        ) {
            return (List<Channel>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
