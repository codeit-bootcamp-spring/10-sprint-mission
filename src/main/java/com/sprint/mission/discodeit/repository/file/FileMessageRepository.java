package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.MessageNotFoundException;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.*;

@Repository
@RequiredArgsConstructor
public class FileMessageRepository implements MessageRepository {

    private static final String FILE_PATH = "messages.dat";

    private final UserService userService;
    private final ChannelService channelService;

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
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // 초기화
    public void resetMessageFile() {
        save(new LinkedHashMap<>());
    }

    @Override
    public Message createMessage(UUID userId, UUID channelId, String content) {
        // 1. 유저와 채널 객체 조회
        User sender = userService.findUser(userId);
        Channel channel = channelService.findChannel(channelId);

        // 2. 메시지 생성 및 저장 (Repository 책임)
        Map<UUID, Message> messages = load(); // 파일에서 기존 메시지 불러오기
        Message message = new Message(sender, channel, content); // 실제 객체로 메시지 생성
        messages.put(message.getId(), message); // Map에 저장
        save(messages); // 파일에 저장

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
        channelService.findChannel(channelId);
        List<Message> result = new ArrayList<>();
        for (Message m : load().values()) {
            if (m.getChannel().getId().equals(channelId)) result.add(m);
        }
        return result;
    }

    @Override
    public Message updateMessage(UUID messageId, String newContent) {
        Map<UUID, Message> messages = load();
        Message message = messages.get(messageId);
        if (message == null) throw new MessageNotFoundException();

        message.updateContent(newContent);
        save(messages);
        return message;
    }

    @Override
    public void deleteMessage(UUID messageId) {
        Map<UUID, Message> messages = load();
        if (messages.remove(messageId) == null) throw new MessageNotFoundException();
        save(messages);
    }
}
