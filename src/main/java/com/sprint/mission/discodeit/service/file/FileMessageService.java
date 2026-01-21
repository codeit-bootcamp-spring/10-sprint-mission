package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class FileMessageService implements MessageService {

    private final List<Message> data = new ArrayList<>();
    private final Path filePath;

    private final UserService userService;
    private final ChannelService channelService;

    public FileMessageService(UserService userService, ChannelService channelService) {
        this.userService = userService;
        this.channelService = channelService;

        this.filePath = Path.of("data", "messages.ser");
        load();
    }

    private void load() {
        if (Files.notExists(filePath)) return;

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath.toFile()))) {
            @SuppressWarnings("unchecked")
            List<Message> loaded = (List<Message>) ois.readObject();
            data.clear();
            data.addAll(loaded);
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void save() {
        try {
            Files.createDirectories(filePath.getParent());
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath.toFile()))) {
                oos.writeObject(data);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Message createMessage(UUID userId, UUID channelId, String content) {
        User user = userService.findById(userId);
        Channel channel = channelService.findById(channelId);

        Message message = new Message(user, channel, content);
        data.add(message);
        save();
        return message;
    }

    @Override
    public Message findById(UUID id) {
        return data.stream()
                .filter(m -> m.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Message not found: " + id));
    }

    @Override
    public List<Message> findAll() {
        return new ArrayList<>(data);
    }

    @Override
    public void updateMessage(UUID id, String content, UUID userId, UUID channelId) {
        Message message = findById(id);

        Optional.ofNullable(content).ifPresent(message::setContent);

        if (userId != null) {
            User user = userService.findById(userId);
            message.setUser(user);
        }
        if (channelId != null) {
            Channel channel = channelService.findById(channelId);
            message.setChannel(channel);
        }

        message.touch();
        save();
    }

    @Override
    public void delete(UUID id) {
        Message message = findById(id);
        data.remove(message);
        save();
    }
}
