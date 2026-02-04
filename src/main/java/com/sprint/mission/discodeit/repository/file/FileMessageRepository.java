package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Repository("messageRepository")
public class FileMessageRepository implements MessageRepository {
    private List<Message> data;

    public FileMessageRepository() {
        this.data = new ArrayList<>();
    }

    // 직렬화 - 저장 로직
    public void serialize(List<Message> messages) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("messageList.ser"))) {
            oos.writeObject(messages);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 역직렬화 - 저장 로직
    public List<Message> deserialize() {
        File file = new File("messageList.ser");
        List<Message> newMessage = new ArrayList<>();

        if (!file.exists()) {
            return newMessage;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            newMessage = (List<Message>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("역직렬화가 안됨");
        }
        return newMessage;
    }

    @Override
    public void save(Message message) {
        this.data.add(message);
        serialize(this.data);
    }

    @Override
    public void delete(UUID messageId) {
        this.data = deserialize();
        for (Message message : this.data) {
            if (message.getId().equals(messageId)) {
                this.data.remove(message);
                serialize(this.data);
                break;
            }
        }
    }

    @Override
    public void deleteByChannelId(UUID channelId) {
        this.data = deserialize();
        for (Message message : this.data) {
            if (message.getChannelId().equals(channelId)) {
                this.data.remove(message);
                serialize(this.data);
                break;
            }
        }
    }

    public Message updateMessageData(UUID messageId, String messageData) {
        this.data = deserialize();
        for (Message message : this.data) {
            if (message.getId().equals(messageId)) {
                message.updateText(messageData);
                serialize(this.data);
                return message;
            }
        }
        throw new NoSuchElementException();
    }

    @Override
    public List<Message> loadAll() {
        return deserialize();
    }

    @Override
    public Message loadById(UUID messageId) {
        this.data = deserialize();
        for (Message message : this.data) {
            if (message.getId().equals(messageId)) {
                return message;
            }
        }
        throw new NoSuchElementException();
    }
}
