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
import java.util.Optional;
import java.util.UUID;

public class FileChannelService implements ChannelService {

    private static final String FILE_NAME = "channels.ser";
    private final Path filePath;
    private final UserService userService;

    public FileChannelService(UserService userService, String path) {
        this.filePath = Paths.get(path, FILE_NAME);
        this.userService = userService;
        init(filePath.getParent());
    }

    @Override
    public Channel create(ChannelType type, String name, String description) {
        List<Channel> data = load();
        existsByChannelName(data, name);
        Channel channel = new Channel(type, name, description);
        data.add(channel);
        save(data);
        return channel;
    }

    @Override
    public Channel findChannelById(UUID channelId) {
        List<Channel> data = load();
        return data.stream()
                .filter(channel -> channel.getId().equals(channelId))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채널입니다."));
    }

    @Override
    public Channel findChannelByName(String name) {
        List<Channel> data = load();
        return data.stream()
                .filter(channel -> channel.getChannelName().equals(name))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채널입니다."));
    }

    @Override
    public List<Channel> findAllChannel() {
        return load();
    }

    //특정 사용자의 참가한 채널 리스트 조회
    @Override
    public List<Channel> findChannelsByUser(UUID userId) {
        List<Channel> data = load();
        return data.stream()
                .filter(channel -> channel.getUsers().stream()
                        .anyMatch(user -> user.getId().equals(userId)))
                .toList();
    }

    @Override
    public Channel update(UUID channelId, String name, String description) {
        List<Channel> data = load();
        Channel channel = findInList(data, channelId);

        if (name != null && !name.equals(channel.getChannelName())) {
            existsByChannelName(data, name);
        }
        Optional.ofNullable(name).ifPresent(channel::updateChannelName);
        Optional.ofNullable(description).ifPresent(channel::updateDescription);

        saveOrUpdate(channel);
        return channel;
    }

    @Override
    public void delete(UUID channelId) {
        List<Channel> data = load();
        Channel channel = findInList(data, channelId);

        channel.getUsers().forEach(user -> {
            user.leave(channel);
            channel.leave(user);
            userService.saveOrUpdate(user);
        });
        data.remove(channel);
        save(data);
    }

    @Override
    public void saveOrUpdate(Channel channel) {
        List<Channel> data = load();
        data.removeIf(c -> c.getId().equals(channel.getId()));
        data.add(channel);
        save(data);
    }

    @Override
    public void joinChannel(UUID channelId, UUID userId) {
        Channel channel = findChannelById(channelId);
        User user = userService.findUserById(userId);

        channel.join(user);
        user.join(channel);

        saveOrUpdate(channel);
        userService.saveOrUpdate(user);
    }

    @Override
    public void leaveChannel(UUID channelId, UUID userId) {
        Channel channel = findChannelById(channelId);
        User user = userService.findUserById(userId);

        channel.leave(user);
        user.leave(channel);

        saveOrUpdate(channel);
        userService.saveOrUpdate(user);
    }


    //채널명 중복체크
    private void existsByChannelName(List<Channel> data, String name) {
        boolean exist = data.stream().anyMatch(channel -> channel.getChannelName().equals(name));
        if (exist) {
            throw new IllegalArgumentException("이미 사용중인 채널명입니다: " + name);
        }
    }

    //내부에서 수정, 삭제를 위한 조회메서드
    private Channel findInList(List<Channel> data, UUID channelId) {
        return data.stream()
                .filter(channel -> channel.getId().equals(channelId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채널입니다."));
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

    private void save(List<Channel> data) {

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
