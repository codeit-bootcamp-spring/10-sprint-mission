package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.io.*;
import java.util.*;

public class FileMessageService implements MessageService {

    private final String FILE_PATH = "messages.dat";
    private final Map<UUID, Message> data;

    private final UserService userService;
    private final ChannelService channelService;

    public FileMessageService(UserService userService, ChannelService channelService) {
        this.userService = userService;
        this.channelService = channelService;
        this.data = loadFromFile();
    }

    @Override
    public Message create(String content, UUID userId, UUID channelId) {
        User user = userService.findById(userId);
        Channel channel = channelService.findById(channelId);

        Objects.requireNonNull(content, "메시지 내용은 null일 수 없습니다.");
        Objects.requireNonNull(user, "메시지 작성자(User)는 null일 수 없습니다.");
        Objects.requireNonNull(channel, "메시지 대상 채널(Channel)은 null일 수 없습니다.");

        if (!channel.getUsers().contains(user)) {
            throw new IllegalArgumentException("해당 채널의 멤버가 아니면 메시지를 작성할 수 없습니다.");
        }

        Message message = new Message(content, user, channel);

        data.put(message.getId(), message);
        channel.addMessage(message);

        saveToFile();
        channelService.update(null, null, null);

        return message;
    }

    @Override
    public Message findById(UUID messageId) {
        Objects.requireNonNull(messageId, "조회하려는 메시지 Id가 null입니다.");
        Message message = data.get(messageId);
        return Objects.requireNonNull(message, "Id에 해당하는 메세지가 존재하지 않습니다.");
    }

    @Override
    public List<Message> findAllByChannelId(UUID channelId) {
        Objects.requireNonNull(channelId, "조회하려는 채널 Id가 null입니다.");
        Channel channel = channelService.findById(channelId);

        if (channel == null) {
            return List.of();
        }
        return channel.getMessages();
    }

    @Override
    public Message update(UUID messageId, String content) {
        Message message = findById(messageId);
        Optional.ofNullable(content).ifPresent(message::updateContent);

        saveToFile();

        return message;
    }

    @Override
    public void delete(UUID messageId) {
        Message message = findById(messageId);

        Channel channel = channelService.findById(message.getChannelId());
        if (channel != null) {
            channel.removeMessage(messageId);
            channelService.update(null, null, null);    // update로 채널 영속화
        }

        data.remove(messageId);
        saveToFile();
    }

    @Override
    public List<Message> getMessageListByChannelId(UUID channelId) {
        Objects.requireNonNull(channelId, "채널 Id가 유효하지 않습니다.");
        Channel channel = channelService.findById(channelId);
        return channel.getMessages();
    }

    private void saveToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(data);
        } catch (IOException e) {
            System.err.println("메시지 데이터 저장 중 오류 발생: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private Map<UUID, Message> loadFromFile() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            return new HashMap<>();
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (Map<UUID, Message>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("메시지 데이터 로드 중 오류 발생: " + e.getMessage());
            return new HashMap<>();
        }
    }
}