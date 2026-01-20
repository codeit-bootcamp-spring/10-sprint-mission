package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.io.*;
import java.util.*;

public class FileMessageService implements MessageService {
    private static final String FILE_PATH = "messages.ser";
    private final UserService userService;
    private ChannelService channelService;

    public void setChannelService(ChannelService channelService) {
        this.channelService = channelService;
    }

    private Map<UUID, Message> data;

    public FileMessageService(UserService userService) {
        this.userService = userService;
        this.data = loadMessages();
    }

    @Override
    public Message sendMessage(UUID userId, UUID channelId, String content) {
        User userInfo = userService.getUserInfoByUserId(userId);
        Message message = new Message(userInfo, channelService.getChannelInfoById(channelId), content);
        // 메시지 생성 및 리스트에 추가
        data.put(message.getId(), message);
        userInfo.updateSentMessages(message);
        saveMessages();

        return message;
    }

    @Override
    public List<Message> getAllMessages() {
        return data.values().stream().toList();
    }

    // 유저 아이디에 따라 메시지 리스트 반환
    @Override
    public List<Message> getMessageListByUser(UUID userId) {
        Objects.requireNonNull(userId, "userId는 null일 수 없습니다.");
        // 유저가 존재하지 않을 경우 예외 처리
        if (userService.getUserInfoByUserId(userId) == null) {
            throw new NoSuchElementException("해당 id를 가진 유저가 존재하지 않습니다.");
        }

        return data.values().stream()
                .filter(id -> id.getSentUser().getId().equals(userId))
                .toList();
    }

    // 채널 아이디에 따라 메시지 리스트 반환
    @Override
    public List<Message> getMessageListByChannel(UUID channelId) {
        Objects.requireNonNull(channelId, "channelId는 null일 수 없습니다.");
        // 채널이 존재하지 않을 경우 예외 처리
        if (channelService.getChannelInfoById(channelId) == null) {
            throw new NoSuchElementException("해당 id를 가진 채널이 존재하지 않습니다.");
        }

        return data.values().stream()
                .filter(id -> id.getSentChannel().getId().equals(channelId))
                .toList();
    }

    // 메시지 아이디에 따라 해당 메시지 반환
    @Override
    public Message getMessageByMessageId(UUID messageId) {
        return findMessageById(messageId);
    }

    @Override
    public Message updateMessage(UUID messageId, String newContent) {
        Message message = findMessageById(messageId);

        message.updateContent(newContent);
        saveMessages();
        return message;
    }

    @Override
    public void deleteMessage(UUID messageId) {
        findMessageById(messageId);
        Message message = data.remove(messageId);

        message.getSentUser().removeSentMessage(message);
        saveMessages();
    }

    @Override
    public void clearChannelMessage(UUID channelId) {
        List<UUID> messageId = data.values().stream()
                .filter(message -> message.getSentChannel().getId().equals(channelId))
                .map(Message::getId)
                .toList();

        if (messageId.isEmpty()) {
            throw new NoSuchElementException("해당 채널에 메시지가 없습니다");
        }

        messageId.forEach(data::remove);
        saveMessages();
    }

    private Message findMessageById(UUID messageId) {
        Objects.requireNonNull(messageId, "messageId는 null일 수 없습니다.");

        Message message = data.get(messageId);

        if (message == null) {
            throw new NoSuchElementException("해당 id를 가진 메시지가 존재하지 않습니다.");
        }

        return message;
    }

    @SuppressWarnings("unchecked")
    private Map<UUID, Message> loadMessages() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            return new HashMap<>();
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (Map<UUID, Message>) ois.readObject();
        } catch (Exception e) {
            throw new RuntimeException("메시지 데이터 로드 실패", e);
        }
    }

    private void saveMessages() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(data);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
