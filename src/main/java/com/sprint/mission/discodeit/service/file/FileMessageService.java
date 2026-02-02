package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.dto.UserResponse;
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
    public Message create(String content, UUID channelId, UUID authorId) {
        UserResponse author = userService.find(authorId);
        Channel channel = channelService.find(channelId);

        Message message = new Message(content,authorId, channelId);
        data.add(message);
        save();
        return message;
    }

    @Override
    public Message find(UUID messageId) {
        return data.stream()
                .filter(m -> m.getId().equals(messageId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Message not found: " + messageId));
    }

    @Override
    public List<Message> findAll() {
        return new ArrayList<>(data);
    }

    @Override
    public Message update(UUID messageId, String newContent) {
        Message message = find(messageId);

        Optional.ofNullable(newContent).ifPresent(message::setContent);

        message.touch();
        save();
        return message;
    }

    @Override
    public void delete(UUID messageId) {
        Message message = find(messageId);
        data.remove(message);
        save();
    }
}
