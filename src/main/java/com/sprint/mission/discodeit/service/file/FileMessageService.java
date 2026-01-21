package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.util.Validators;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FileMessageService implements MessageService {
    private static final String FILE_PATH = "data/messages.ser";
    final List<Message> data;
    private final ChannelService channelService;
    private final UserService userService;

    public FileMessageService(UserService userService, ChannelService channelService) {
        this.data = loadMessages();
        this.userService = userService;
        this.channelService = channelService;
    }

    private void saveMessages(){
        File file = new File(FILE_PATH);
        try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(this.data);
            System.out.println("직렬화 완료: messages.ser에 저장되었습니다.");
        } catch (Exception e) {
            throw new RuntimeException("메시지 데이터 저장 중 오류 발생", e);
        }
    }

    @SuppressWarnings("unchecked")
    private List<Message> loadMessages() {
        File file = new File(FILE_PATH);
        if(!file.exists() || file.length() == 0) {
            return new ArrayList<>();
        }
        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            Object data = ois.readObject();
            System.out.println("역직렬화 완료: " + data);
            return (List<Message>) data;
        } catch (IOException |ClassNotFoundException e) {
            throw new RuntimeException("메시지 데이터 로드 중 오류 발생", e);
        }
    }



    @Override
    public Message createMessage(String content, UUID channelId, UUID userId) {
        Validators.validationMessage(content);
        Channel channel = channelService.readChannel(channelId);
        User user = userService.readUser(userId);
        Message message = new Message(content, channel, user);

        channel.getMessages().add(message);
        user.getMessages().add(message);

        this.data.add(message);
        saveMessages();
        channelService.updateChannel(channel.getId(), null, null, null);
        userService.updateUser(user.getId(), null, null);

        return message;
    }

    @Override
    public Message readMessage(UUID id) {
        return validateExistenceMessage(id);
    }

    @Override
    public List<Message> readAllMessage() {
        return this.data;
    }

    @Override
    public Message updateMessage(UUID id, String content) {
        Message message = validateExistenceMessage(id);
        Optional.ofNullable(content)
                .ifPresent(cont -> {Validators.requireNotBlank(cont, "content");
                    message.updateContent(content);
                });

        saveMessages();
        return message;
    }

    public void deleteMessage(UUID id) {
        Message message = validateExistenceMessage(id);
        Channel channel = message.getChannel();
        User user = message.getUser();

        channel.getMessages().removeIf(m -> id.equals(m.getId()));
        user.getMessages().removeIf(m -> id.equals(m.getId()));
        this.data.removeIf(m -> id.equals(m.getId()));
        saveMessages();
    }

    public List<Message> readMessagesByChannel(UUID channelId) {
        Channel channel = channelService.readChannel(channelId);
        return channel.getMessages();
    }

    public List<Message> readMessagesByUser(UUID userId) {
        User user = userService.readUser(userId);
        return user.getMessages();
    }
    private Message validateExistenceMessage(UUID id) {
        Validators.requireNonNull(id, "id는 null이 될 수 없습니다.");
        return this.data.stream()
                .filter(message -> id.equals(message.getId()))

                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("메세지 id는 존재하지 않습니다."));
    }

}
