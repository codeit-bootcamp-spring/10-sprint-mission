package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class FileMessageService implements MessageService {
    private UserService userService;
    private ChannelService channelService;

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public void setChannelService(ChannelService channelService) {
        this.channelService = channelService;
    }

    @Override
    public Message createMessage(String content, User sender, Channel channel) {
        Message message = new Message(content, sender, channel);
        save(message);
        return message;
    }

    @Override
    public Message getMessage(UUID messageId) {
        Path messagePath = getMessagePath(messageId);
        if(!messagePath.toFile().exists()) {
            throw new NoSuchElementException("해당 메세지가 존재하지 않습니다.");
        }
        try (FileInputStream fis = new FileInputStream(messagePath.toFile());
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            return (Message) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Message> getAllMessages() {
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
                                throw new RuntimeException(e);
                            }
                        })
                        .toList();
                return messages;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public List<Message> getMessagesByUserId(UUID userId) {
        return userService.getUser(userId).getMessages();
    }

    @Override
    public List<Message> getMessagesByChannelId(UUID channelId) {
        return channelService.getChannel(channelId).getMessages();
    }

    @Override
    public Message updateMessage(UUID messageId, String content) {
        Message updateMessage = getMessage(messageId);
        Optional.ofNullable(content)
                .ifPresent(updateMessage::updateContent);
        save(updateMessage);
        return updateMessage;
    }

    @Override
    public void deleteMessage(UUID messageId) {
        Path messagePath = getMessagePath(messageId);
        try {
            Files.delete(messagePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Path getMessagePath(UUID messagelId) {
        return Paths.get("messages", messagelId.toString() + ".ser");
    }

    private void save(Message message) {
        Path messagePath = getMessagePath(message.getId());
        try (
                FileOutputStream fos = new FileOutputStream(messagePath.toFile());
                ObjectOutputStream oos = new ObjectOutputStream(fos)
        ) {
            oos.writeObject(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
