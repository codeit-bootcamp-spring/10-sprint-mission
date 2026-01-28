package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.MessageService;
import java.io.*;
import java.util.*;

public class FileMessageService implements MessageService {
    private final String FILE_PATH = "messages.dat";

    private final FileUserService userService = new FileUserService();
    private final FileChannelService channelService = new FileChannelService();

    @SuppressWarnings("unchecked")
    private List<Message> loadMessages() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (List<Message>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new ArrayList<>();
        }
    }

    private void saveMessages(List<Message> messages) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(messages);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Message create(UUID userId, UUID channelId, String content) {
        List<Message> messages = loadMessages();

        User author = userService.findById(userId);
        Channel channel = channelService.findById(channelId);

        if (author == null || channel == null) {
            throw new IllegalArgumentException("존재하지 않는 유저 또는 채널입니다.");
        }

        Message newMessage = new Message(author, channel, content);

        messages.add(newMessage);
        saveMessages(messages);
        return newMessage;
    }

    @Override
    public Message findById(UUID messageId) {
        return loadMessages().stream()
                .filter(m -> m.getId().equals(messageId))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Message> findAll() {
        return loadMessages();
    }

    @Override
    public Message update(UUID messageId, String content) {
        List<Message> messages = loadMessages();
        for (Message message : messages) {
            if (message.getId().equals(messageId)) {
                message.updateContent(content);
                saveMessages(messages);
                return message;
            }
        }
        return null;
    }

    @Override
    public void delete(UUID messageId) {
        List<Message> messages = loadMessages();
        messages.removeIf(m -> m.getId().equals(messageId));
        saveMessages(messages);
    }

    @Override
    public List<Message> findMessagesByChannelId(UUID channelId) {
        List<Message> result = new ArrayList<>();
        for (Message message : loadMessages()) {
            if (message.getChannel().getId().equals(channelId)) {
                result.add(message);
            }
        }
        return result;
    }
}