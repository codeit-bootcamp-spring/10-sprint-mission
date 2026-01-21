package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.io.*;
import java.util.*;

public class FileMessageRepository implements MessageRepository {
    private Map<UUID, Message> data;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    public FileMessageRepository(UserRepository userRepository,ChannelRepository channelRepository) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("./messages.ser"))) {
            data = (Map<UUID, Message>) ois.readObject();
        }catch (IOException | ClassNotFoundException e) {
            System.out.println(e.getMessage());
            data = new HashMap<>();
        }
        saveData();
        this.userRepository = userRepository;
        this.channelRepository = channelRepository;
    }

    @Override
    public Message save(Message message) {
        loadData();
        data.put(message.getId(), message);
        saveData();
        return message;
    }

    @Override
    public Optional<Message> findById(UUID id) {
        loadData();
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public List<Message> findAll() {
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
    public List<Message> getMessagesByUserId(UUID userId) {
        loadData();
        User user = userRepository.findById(userId).orElse(null);
        return data.values().stream()
                .filter(message -> message.getUser().equals(user))
                .toList();
    }

    @Override
    public List<Message> getMessagesByChannelId(UUID channelId) {
        loadData();
        Channel channel = channelRepository.findById(channelId).orElse(null);
        return data.values().stream()
                .filter(message -> message.getChannel().equals(channel))
                .toList();
    }

    @Override
    public void loadData() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("./messages.ser"))) {
            data = (Map<UUID, Message>) ois.readObject();
        }catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void saveData() {
        try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("./messages.ser"))){
            oos.writeObject(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
