package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.DuplicationChannelException;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class FileChannelRepository implements ChannelRepository {

    private static final String FILE_PATH = "channels.dat";

    private Map<UUID, Channel> load() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return new LinkedHashMap<>();

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (Map<UUID, Channel>) ois.readObject();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void save(Map<UUID, Channel> channels) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(channels);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void resetChannelFile() {
        Map<UUID, Channel> channels = load();
        channels.clear();
        save(channels);
    }

    @Override
    public Channel createChannel(Channel channel) {
        Map<UUID, Channel> channels = load();

        if (channels.values().stream()
                .anyMatch(c -> c.getChannelName().equals(channel.getChannelName()))) {
            throw new DuplicationChannelException();
        }

        channels.put(channel.getId(), channel);
        save(channels);
        return channel;
    }

    @Override
    public Channel findChannel(UUID id) {
        Channel channel = load().get(id);
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
        if (channels.remove(channelId) == null) {
            throw new ChannelNotFoundException();
        }
        save(channels);
    }

    @Override
    public Channel updateChannel(UUID id, Channel channel) {
        Map<UUID, Channel> channels = load();
        if (!channels.containsKey(id)) throw new ChannelNotFoundException();

        channels.put(id, channel);
        save(channels);
        return channel;
    }

    @Override
    public boolean existsByNameChannel(String channelName) {
        return load().values().stream()
                .anyMatch(c -> c.getChannelName().equals(channelName));
    }

    @Override
    public Channel findByUserId(UUID userId) {
        return load().values().stream()
                .filter(c -> c.hasUserId(userId))
                .findFirst()
                .orElseThrow(ChannelNotFoundException::new);
    }

    @Override
    public String findAllUserInChannel(UUID channelId) {
        Channel channel = findChannel(channelId);
        List<User> users = channel.getChannelUser();

        return users.isEmpty()
                ? "(참여자 없음)"
                : users.stream().map(User::getUserName).collect(Collectors.joining(", "));
    }

    @Override
    public Channel saveChannel(Channel channel) {
        Map<UUID, Channel> channels = load();
        channels.put(channel.getId(), channel);
        save(channels);
        return channel;
    }
}
