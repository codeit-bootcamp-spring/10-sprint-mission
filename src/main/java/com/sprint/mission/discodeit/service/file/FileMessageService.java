package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.io.*;
import java.util.*;

public class FileMessageService implements MessageService {
    private final String FILE_PATH = "messages.dat";
    private final ChannelService channelService;
    private final UserService userService;

    public FileMessageService(UserService userService, ChannelService channelService) {
        this.userService = userService;
        this.channelService = channelService;
    }

    private Map<UUID, Message> loadData() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return new HashMap<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (Map<UUID, Message>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new HashMap<>();
        }
    }

    private void saveData(Map<UUID, Message> data) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Message createMessage(UUID channelId, UUID userId, String content) {
        Channel channel = channelService.getChannel(channelId);
        User user = userService.getUser(userId);
        if (!channel.getUsers().contains(user)) throw new IllegalArgumentException("채널에 먼저 입장해야 메시지를 남길 수 있습니다.");
        validateMessageContent(content);

        Map<UUID, Message> data = loadData();
        Message message = new Message(channel, user, content);
        data.put(message.getId(), message);

        channel.addMessage(message);
        user.addMessage(message);
        saveData(data);
        return message;
    }

    @Override
    public Message getMessage(UUID id) {
        return validateMessageId(id);
    }

    @Override
    public List<Message> getAllMessages() {
        return new ArrayList<>(loadData().values());
    }

    @Override
    public Message updateMessage(String content, UUID id) {
        Map<UUID, Message> data = loadData();
        Message message = validateMessageId(id, data);
        Optional.ofNullable(content).filter(c -> !c.isBlank()).ifPresent(message::updateContent);
        saveData(data);
        return message;
    }

    @Override
    public void deleteMessage(UUID id) {
        Map<UUID, Message> data = loadData();
        Message message = validateMessageId(id, data);
        message.getChannel().removeMessage(message);
        message.getUser().removeMessage(message);
        data.remove(id);
        saveData(data);
    }

    @Override
    public List<Message> getMessagesByChannelId(UUID channelId) {
        return new ArrayList<>(channelService.getChannel(channelId).getMessages());
    }

    @Override
    public List<Message> getMessagesByUserId(UUID userId) {
        return new ArrayList<>(userService.getUser(userId).getMessages());
    }

    private void validateMessageContent(String content) {
        if (content == null || content.isBlank()) throw new IllegalArgumentException("내용을 다시 입력해주세요");
    }

    private Message validateMessageId(UUID id) {
        return validateMessageId(id, loadData());
    }

    private Message validateMessageId(UUID id, Map<UUID, Message> data) {
        return Optional.ofNullable(data.get(id))
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 메시지입니다"));
    }
}
