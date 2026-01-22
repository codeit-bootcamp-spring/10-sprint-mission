package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.MessageNotFoundException;
import com.sprint.mission.discodeit.exception.MessageNotInputException;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.io.*;
import java.nio.channels.FileLockInterruptionException;
import java.util.*;

public class FileMessageService implements MessageService {
    private static final String FILE_PATH = "message.dat";
    private final UserService userService;
    private final ChannelService channelService;

    public FileMessageService(UserService userService, ChannelService channelService) {
        this.userService = userService;
        this.channelService = channelService;
    }
    public UserService getUserService() {
        return this.userService;
    }

    private Map<UUID, Message> load(){
        File file  = new File(FILE_PATH);
        if (!file.exists()){
            return new LinkedHashMap<>();
        }

        try (ObjectInputStream ois =
                new ObjectInputStream(new FileInputStream(file))){
            return (Map<UUID, Message>) ois.readObject();
        } catch (Exception e){
            throw  new RuntimeException(e);
        }
    }

    private void save(Map<UUID, Message> messages){
        try (ObjectOutputStream oos =
                     new ObjectOutputStream(new FileOutputStream(FILE_PATH))){
            oos.writeObject(messages);
        } catch (Exception e){
            throw  new RuntimeException(e);
        }
    }

    // 파일 초기화
    public void resetMessageFile() {
        try {
            File file = new File(FILE_PATH);
            if (file.exists()) file.delete();
            save(new LinkedHashMap<>());
        } catch (Exception e) {
            throw new RuntimeException("Message 파일 초기화 실패", e);
        }
    }


    @Override
    public Message createMessage(User user, Channel channel, String content) {
        Map<UUID, Message> messages = load();

        userService.findUser(user.getId());
        channelService.findChannel(channel.getId());

        Message message = new Message(user, channel, content);
        messages.put(message.getId(), message);

        save(messages);
        return message;
    }

    @Override
    public Message findMessage(UUID messageId) {
        Map<UUID, Message> messages = load();

        Message message = messages.get(messageId);
        if (message == null){
            throw new MessageNotFoundException();
        }

        return message;
    }

    @Override
    public List<Message> findAllMessage() {
        Map<UUID, Message> messages = load();

        return new ArrayList<>(messages.values());
    }

    public List<Message> findAllByUserMessage(UUID userId){
        Map<UUID, Message> messages = load();
        userService.findUser(userId);

        List<Message> userMessages = messages.values().stream()
                .filter(message -> message.getSender().getId().equals(userId))
                .toList();

        if (userMessages.isEmpty()) {
            throw new MessageNotFoundException();
        }

        return userMessages;
    }

    @Override
    public List<Message> findAllByChannelMessage(UUID channelId) {
        Map<UUID, Message> messages = load();

        channelService.findChannel(channelId);

        return messages.values().stream()
                .filter(m -> m.getChannel().getId().equals(channelId))
                .toList();
    }

    @Override
    public Message updateMessage(UUID messageId, String newContent) {
        Map<UUID, Message> messages = load();

        Message message = messages.get(messageId);
        if (message == null){
            throw new MessageNotInputException();
        }

        message.updateContent(newContent);

        save(messages);
        return message;

    }

    @Override
    public void deleteMessage(UUID messageId) {
        Map<UUID, Message> messages = load();

        if(messages.remove(messageId) == null){
            throw new MessageNotFoundException();
        }

        save(messages);
    }
}
