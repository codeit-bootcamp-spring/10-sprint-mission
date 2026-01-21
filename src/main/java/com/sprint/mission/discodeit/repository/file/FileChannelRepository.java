package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class FileChannelRepository implements ChannelRepository {
    private Map<UUID, Channel> channelDB = new HashMap<>();
    private final File file = new File("channels.ser");

    public FileChannelRepository() {
        if (file.exists()) load(); else persist();
    }

    @SuppressWarnings("unchecked")
    private void load() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            this.channelDB = (Map<UUID, Channel>) ois.readObject();
        } catch (Exception e) {
            System.err.println("Channel 데이터 로드 실패: " + e.getMessage());
            this.channelDB = new HashMap<>();
        }
    }

    private void persist() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(this.channelDB);
        } catch (IOException e) {
            System.err.println("Channel 데이터 저장 실패: " + e.getMessage());
        }
    }

    @Override
    public Channel save(Channel channel) {
        channelDB.put(channel.getId(), channel);
        persist();
        return channel;
    }

    @Override
    public Optional<Channel> findById(UUID id) {
        return Optional.ofNullable(channelDB.get(id));
    }

    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(channelDB.values());
    }

    @Override
    public void delete(UUID id) {
        channelDB.remove(id);
        persist();
    }

    @Override
    public boolean existsByName(String name) {
        return channelDB.values().stream().anyMatch(ch -> ch.getChannelName().equals(name));
    }

    @Override
    public List<Channel> findAllByOwnerId(UUID ownerId) {
        return channelDB.values().stream()
                .filter(ch -> ch.getOwner().getId().equals(ownerId))
                .collect(Collectors.toList());
    }
}