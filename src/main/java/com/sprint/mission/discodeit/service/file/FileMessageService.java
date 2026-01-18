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

public class FileMessageService implements MessageService {

    private final Path filePath;
    private final ChannelService channelService;
    private final UserService userService;

    public FileMessageService(ChannelService channelService, UserService userService, String path) {
        this.filePath = Paths.get(path, "messages.ser");
        this.channelService = channelService;
        this.userService = userService;
        init(filePath.getParent());
    }

    @Override
    public Message create(String text, UUID channelId, UUID userId) {
        channelService.joinChannel(channelId, userId);

        List<Message> data = load();

        User user = userService.findUserById(userId);
        Channel channel = channelService.findChannelById(channelId);

        Message message = new Message(text, user, channel);

        data.add(message);
        save(data);
        return message;
    }

    //특정 채널에 특정 유저가 쓴 메세지 리스트 반환
    @Override
    public List<Message> findMessagesByUserAndChannel(UUID channelId, UUID userId) {
        List<Message> data = load();
        return data.stream().filter(message -> message.getUser().getId().equals(userId))
                .filter(message -> message.getChannel().getId().equals(channelId))
                .toList();
    }

    //특정 채널에 발행된 메시지 리스트 조회
    @Override
    public List<Message> findMessagesByChannel(UUID channelId) {
        List<Message> data = load();
        return data.stream()
                .filter(message -> message.getChannel().getId().equals(channelId))
                .toList();
    }

    //특정 사용자의 발행한 메시지 리스트 조회
    @Override
    public List<Message> findMessagesByUser(UUID userId) {
        List<Message> data = load();
        return data.stream()
                .filter(message -> message.getUser().getId().equals(userId))
                .toList();
    }

    //모든 채널의 모든 메세지리스트 반환
    @Override
    public List<Message> findAllMessages() {
        return load();
    }

    @Override
    public Message findMessage(UUID messageId) {
        List<Message> data = load();
        return data.stream().filter(message -> message.getId().equals(messageId))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 메세지 아이디입니다."));
    }

    @Override
    public Message update(UUID messageId, String text) {
        List<Message> data = load();
        Message message = findInList(data, messageId);
        message.update(text);
        saveOrUpdate(message);
        return message;
    }

    @Override
    public void saveOrUpdate(Message message) {
        List<Message> data = load();
        data.removeIf(m -> m.getId().equals(message.getId()));
        data.add(message);
        save(data);
    }

    @Override
    public void delete(UUID messageId) {
        List<Message> data = load();
        Message message = findInList(data, messageId);

        data.remove(message);
        save(data);
    }

    //내부에서 수정, 삭제를 위한 조회메서드
    private Message findInList(List<Message> data, UUID messageId) {
        return data.stream()
                .filter(message -> message.getId().equals(messageId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 메세지입니다."));
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

    private void save(List<Message> data) {
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
