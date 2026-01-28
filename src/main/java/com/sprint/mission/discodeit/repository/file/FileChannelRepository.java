package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.AlreadyJoinedChannelException;
import com.sprint.mission.discodeit.exception.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.UserNotInChannelException;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.service.UserService;

import java.io.*;
import java.util.*;

public class FileChannelRepository implements ChannelRepository {

    private static final String FILE_PATH = "channels.dat";
    private final UserService userService;

    public FileChannelRepository(UserService userService) {
        this.userService = userService;
    }

    // 파일에서 Map 로드
    private Map<UUID, Channel> load() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return new LinkedHashMap<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (Map<UUID, Channel>) ois.readObject();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public void userAddChannel(UUID channelId, UUID userId) {
        Map<UUID, Channel> channels = load();
        Channel channel = channels.get(channelId);
        if (channel == null) throw new ChannelNotFoundException();

        User user = userService.findUser(userId);
        if (channel.getChannelUser().contains(user)) throw new AlreadyJoinedChannelException();

        channel.getChannelUser().add(user);
        save(channels);
    }

    // 파일에 Map 저장
    private void save(Map<UUID, Channel> channels) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(channels);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // 초기화
    public void resetChannelFile() {
        save(new LinkedHashMap<>());
    }

    @Override public Channel createChannel(Channel channel) {
        Map<UUID, Channel> channels = load();

        // 맵에 저장 후 파일에 저장
        channels.put(channel.getId(), channel);
        save(channels);

        return channel;
    }

    @Override
    public Channel findChannel(UUID channelId) {
        Channel channel = load().get(channelId);
        if (channel == null) throw new ChannelNotFoundException();
        return channel;
    }

    @Override
    public List<Channel> findAllChannel() {
        return new ArrayList<>(load().values());
    }

    @Override
    public void deleteChannel(UUID channelId) {
        Map<UUID, Channel> channels = load();
        if (channels.remove(channelId) == null) throw new ChannelNotFoundException();
        save(channels);
    }


    @Override
    public boolean existsByNameChannel(String channelName) {
        return load().values().stream()
                .anyMatch(c -> c.getChannelName().equals(channelName));
    }


    @Override
    public Channel findByUserId(UUID userId) {
        userService.findUser(userId);
        return load().values().stream()
                .filter(channel -> channel.getChannelUser().stream()
                        .anyMatch(user -> user.getId().equals(userId)))
                .findFirst()
                .orElseThrow(ChannelNotFoundException::new);
    }

    @Override
    public String findAllUserInChannel(UUID channelId) {
        Channel channel = findChannel(channelId);
        List<User> users = channel.getChannelUser();
        if (users.isEmpty()) throw new UserNotInChannelException();
        StringBuilder sb = new StringBuilder();
        for (User user : users) {
            sb.append(user.getUserName()).append(", ");
        }
        return sb.substring(0, sb.length() - 2); // 마지막 ", " 제거
    }
}
