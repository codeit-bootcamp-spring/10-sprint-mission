package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class FileMessageRepository implements MessageRepository {
    @Override
    public Optional<Message> findById(UUID messageId) {
        Path messagePath = getMessagePath(messageId);

        if (!Files.exists(messagePath)) {
            return Optional.empty();
        }

        try (FileInputStream fis = new FileInputStream(messagePath.toFile());
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            return Optional.ofNullable((Message) ois.readObject());
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("메세지를 가져오는데 실패했습니다.");
        }
    }

    @Override
    public List<Message> findAll() {
        Path messagePath = Paths.get("messages");
        if(Files.exists(messagePath)) {
            try {
                List<Message> messages = Files.list(messagePath)
                        .map(path -> {
                            try(
                                    FileInputStream fis = new FileInputStream(path.toFile());
                                    ObjectInputStream ois = new ObjectInputStream(fis)
                            ) {
                                Message message = (Message) ois.readObject();
                                return message;
                            } catch (IOException | ClassNotFoundException e) {
                                throw new RuntimeException("모든 메세지를 가져오는데 실패했습니다.");
                            }
                        })
                        .toList();
                return messages;
            } catch (IOException e) {
                throw new RuntimeException("모든 메세지를 가져오는데 실패했습니다.");
            }
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public List<Message> findAllByUserId(UUID userId) {
        return findAll().stream()
                .filter(m -> m.getSenderId().equals(userId))
                .toList();
    }

    @Override
    public List<Message> findAllByChannelId(UUID channelId) {
        return findAll().stream()
                .filter(m -> m.getChannelId().equals(channelId))
                .toList();
    }

    @Override
    public void save(Message message) {
        Path messagePath = getMessagePath(message.getId());
        try (
                FileOutputStream fos = new FileOutputStream(messagePath.toFile());
                ObjectOutputStream oos = new ObjectOutputStream(fos)
        ) {
            oos.writeObject(message);
        } catch (IOException e) {
            throw new RuntimeException("메세지를 저장하는데 실패했습니다.");
        }
    }

    @Override
    public void deleteById(UUID messageId) {
        Path messagePath = getMessagePath(messageId);
        try {
            Files.delete(messagePath);
        } catch (IOException e) {
            throw new RuntimeException("메세지를 삭제하는데 실패했습니다.");
        }
    }

    private Path getMessagePath(UUID messagelId) {
        return Paths.get("messages", messagelId.toString() + ".ser");
    }
}
