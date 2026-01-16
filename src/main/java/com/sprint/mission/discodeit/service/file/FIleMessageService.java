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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FIleMessageService implements MessageService {

    private final Path filePath;
    private final ChannelService channelService;
    private final UserService userService;
    private final List<Message> data;

    public FIleMessageService(ChannelService channelService, UserService userService, String path) {
        this.filePath = Paths.get(path, "messages.ser");
        this.channelService = channelService;
        this.userService = userService;
        init(filePath.getParent());
        this.data = load();
    }

    @Override
    public Message create(String text, UUID channelId, UUID userId) {
        channelService.join(channelId, userId);
        User user = userService.findUserById(userId);
        Channel channel = channelService.findChannelById(channelId);
        Message message = new Message(text, user, channel);
        data.add(message);
        user.send(message);
        channel.send(message);
        save();
        return message;
    }

    //특정 채널에 특정 유저가 쓴 메세지 리스트 반환
    @Override
    public List<Message> findMessagesByUserAndChannel(UUID channelId, UUID userId) {
        return data.stream().filter(message -> message.getUser().getId().equals(userId))
                .filter(message -> message.getChannel().getId().equals(channelId))
                .toList();
    }

    //특정 채널에 발행된 메시지 리스트 조회
    @Override
    public List<Message> findMessagesByChannel(UUID channelId) {
        return channelService.findChannelById(channelId).getMessages();
    }

    //특정 사용자의 발행한 메시지 리스트 조회
    @Override
    public List<Message> findMessagesByUser(UUID userId) {
        return userService.findUserById(userId).getMessages();
    }

    //모든 채널의 모든 메세지리스트 반환
    @Override
    public List<Message> findAllMessages() {
        return new ArrayList<>(data);
    }

    @Override
    public Message findMessage(UUID messageId) {
        return data.stream().filter(message -> message.getId().equals(messageId))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 메세지 아이디입니다."));
    }

    @Override
    public Message update(UUID messageId, String text) {
        Message message = findMessage(messageId);
        message.update(text);
        save();
        return message;
    }

    @Override
    public void delete(UUID messageId) {
        Message message = findMessage(messageId);
        message.getUser().delete(message);
        message.getChannel().delete(message);
        save();
        data.remove(message);
    }

    private void init(Path directory) {
        if (!Files.exists(directory)) {
            try {
                Files.createDirectories(directory);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void save() {
        try (
                FileOutputStream fos = new FileOutputStream(filePath.toFile());
                ObjectOutputStream oos = new ObjectOutputStream(fos)
        ) {
            oos.writeObject(data);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private List<Message> load() {
        if (!Files.exists(filePath)) {
            return new ArrayList<>();
        }

        try (
                FileInputStream fis = new FileInputStream(filePath.toFile());
                ObjectInputStream ois = new ObjectInputStream(fis)
        ) {
            return (List<Message>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
