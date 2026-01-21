package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.io.*;
import java.util.*;

public class FileChannelRepository implements ChannelRepository {
    private Map<UUID, Channel> data;
    private MessageRepository messageRepository;
    private UserRepository userRepository;

    public FileChannelRepository() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("./channels.ser"))) {
            data = (Map<UUID, Channel>) ois.readObject();
        }catch (IOException | ClassNotFoundException e) {
            System.out.println(e.getMessage());
            data = new HashMap<>();
        }
        saveData();
    }

    @Override
    public Channel Save(Channel channel) {
        loadData();
        data.put(channel.getId(), channel);
        saveData();
        return channel;
    }

    @Override
    public Optional<Channel> findById(UUID id) {
        loadData();
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public List<Channel> findAll() {
        loadData();
        return new ArrayList<>(data.values());
    }

    @Override
    public void deleteById(UUID id) {
        loadData();
        data.remove(id);
        saveData();
    }

    @Override
    public List<Channel> getChannelsByUserId(UUID userId) {
        loadData();
        return data.values().stream()
                .filter(channel ->
                        channel.getJoinedUsers().stream()
                                .anyMatch(user -> user.getId().equals(userId)))
                .toList();
    }

    @Override
    public void setMessageRepository(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @Override
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void loadData() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("./channels.ser"))) {
            data = (Map<UUID, Channel>) ois.readObject();
        }catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void saveData() {
        try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("./channels.ser"))){
            oos.writeObject(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
