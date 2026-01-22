package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.MessageNotFoundException;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.io.*;
import java.util.*;

public class FileMessageRepository implements MessageRepository {

    private static final String FILE_PATH = "message.dat";

    private Map<UUID, Message> load() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return new LinkedHashMap<>();

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (Map<UUID, Message>) ois.readObject();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void save(Map<UUID, Message> messages) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(messages);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void resetMessageFile() {
        Map<UUID, Message> messages = load();
        messages.clear();
        save(messages);
    }

    @Override
    public Message createMessage(User user, Channel channel, String content) {
        Map<UUID, Message> messages = load();
        Message message = new Message(user, channel, content);
        messages.put(message.getId(), message);
        save(messages);
        return message;
    }

    @Override
    public Message findMessage(UUID messageId) {
        Message message = load().get(messageId);
        if (message == null) throw new MessageNotFoundException();
        return message;
    }

    @Override
    public List<Message> findAllMessage() {
        return new ArrayList<>(load().values());
    }

    @Override
    public List<Message> findAllByChannelMessage(UUID channelId) {
        return load().values().stream()
                .filter(m -> m.getChannel().getId().equals(channelId))
                .toList();
    }

    @Override
    public List<Message> findAllByUserMessage(UUID userId) {
        List<Message> result = load().values().stream()
                .filter(m -> m.getSender().getId().equals(userId))
                .toList();

        if (result.isEmpty()) throw new MessageNotFoundException();
        return result;
    }

    @Override
    public Message updateMessage(UUID messageId, String content) {
        Map<UUID, Message> messages = load();
        Message message = messages.get(messageId);
        if (message == null) throw new MessageNotFoundException();

        message.updateContent(content);
        save(messages);
        return message;
    }

    @Override
    public void deleteMessage(UUID messageId) {
        Map<UUID, Message> messages = load();
        if (messages.remove(messageId) == null) {
            throw new MessageNotFoundException();
        }
        save(messages);
    }
}
