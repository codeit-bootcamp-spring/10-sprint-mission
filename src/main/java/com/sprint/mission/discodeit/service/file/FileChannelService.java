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
import java.util.stream.Collectors;

public class FileChannelService implements ChannelService {

    private static final String ROOT_PATH = System.getProperty("user.dir") + "/data";
    private final Path dirPath;
    private final UserService userService;

    public FileChannelService(UserService userService) {
        this(userService, ROOT_PATH);
    }

    public FileChannelService(UserService userService, String rootPath) {
        this.dirPath = Paths.get(rootPath, "channel");
        this.userService = userService;
        init();
    }

    @Override
    public Channel create(ChannelType type, String name, String description) {
        existsByChannelName(name);
        Channel channel = new Channel(type, name, description);
        save(channel);
        return channel;
    }

    @Override
    public Channel findChannelById(UUID channelId) {
        return load().stream()
                .filter(channel -> channel.getId().equals(channelId))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채널입니다."));
    }

    @Override
    public Channel findChannelByName(String name) {
        return load().stream()
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
        return load().stream()
                .filter(channel -> channel.getUsers().stream()
                        .anyMatch(user -> user.getId().equals(userId)))
                .toList();
    }

    @Override
    public Channel update(UUID channelId, String name, String description) {
        Channel channel = findChannelById(channelId);

        if (name != null && !name.equals(channel.getChannelName())) {
            existsByChannelName(name);
        }
        Optional.ofNullable(name).ifPresent(channel::updateChannelName);
        Optional.ofNullable(description).ifPresent(channel::updateDescription);

        save(channel);
        return channel;
    }

    @Override
    public void delete(UUID channelId) {
        Channel channel = findChannelById(channelId);

        new ArrayList<>(channel.getUsers()).forEach(user -> {
            user.leave(channel);
            channel.leave(user);
            //UserService의 .ser파일들을 수정할 수 없음
        });

        File file = new File(dirPath.toFile(), channelId.toString() + ".ser");
        if (file.exists()) {
            file.delete();
        }
    }

    @Override
    public void joinChannel(UUID channelId, UUID userId) {
        Channel channel = findChannelById(channelId);
        User user = userService.findUserById(userId);

        channel.join(user);
        user.join(channel);

        save(channel);
        //UserService의 .ser파일들을 수정할 수 없음
    }

    @Override
    public void leaveChannel(UUID channelId, UUID userId) {
        Channel channel = findChannelById(channelId);
        User user = userService.findUserById(userId);

        channel.leave(user);
        user.leave(channel);

        save(channel);
        //UserService의 .ser파일들을 수정할 수 없음
    }

    private void existsByChannelName(String name) {
        boolean exist = load().stream().anyMatch(channel -> channel.getChannelName().equals(name));
        if (exist) {
            throw new IllegalArgumentException("이미 사용중인 채널명입니다: " + name);
        }
    }

    private void init() {
        if (!Files.exists(dirPath)) {
            try {
                Files.createDirectories(dirPath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void save(Channel channel) {
        File file = new File(dirPath.toFile(), channel.getId().toString() + ".ser");
        try (
                FileOutputStream fos = new FileOutputStream(file);
                ObjectOutputStream oos = new ObjectOutputStream(fos)
        ) {
            oos.writeObject(channel);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private List<Channel> load() {
        if(!Files.exists(dirPath)) {
            return new ArrayList<>();
        }
        try {
            List<Channel> list = Files.list(dirPath)
                    .map(path -> {
                        try (
                                FileInputStream fis = new FileInputStream(path.toFile());
                                ObjectInputStream ois = new ObjectInputStream(fis)
                        ) {
                            Object data = ois.readObject();
                            return (Channel) data;
                        } catch (IOException | ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .collect(Collectors.toList());
            return list;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
