package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.io.*;
import java.util.*;

public class FileUserRepository implements UserRepository {
    private Map<UUID, User> data;
    private ChannelRepository channelRepository;
    private MessageRepository messageRepository;

    public FileUserRepository() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("./users.ser"))) {
            data = (Map<UUID, User>) ois.readObject();
        }catch (IOException | ClassNotFoundException e) {
            System.out.println(e.getMessage());
            data = new HashMap<>();
        }
        saveData();
    }

    @Override
    public User save(User user) {
        loadData();
        data.put(user.getId(), user);
        saveData();
        return user;
    }

    @Override
    public Optional<User> findById(UUID id) {
        loadData();
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public List<User> findAll() {
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
    public List<User> getUsersByChannelId(UUID channelId) {
        loadData();
        return data.values()
                .stream()
                .filter(user ->
                        user.getChannels().
                                stream().
                                anyMatch(channel -> channel.getId().equals(channelId)))
                .toList();
    }

    @Override
    public void setChannelRepository(ChannelRepository channelRepository) {
        this.channelRepository = channelRepository;
    }

    @Override
    public void setMessageRepository(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @Override
    public void saveData() {
        try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("./users.ser"))){
            oos.writeObject(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void loadData() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("./users.ser"))) {
            data = (Map<UUID, User>) ois.readObject();
        }catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
