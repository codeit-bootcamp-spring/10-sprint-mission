package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.util.Validator;

import java.io.*;
import java.util.*;

public class FileMessageService implements MessageService {
    private Map<UUID, Message> data;
    private final UserService userService;
    private final ChannelService channelService;

    public FileMessageService(UserService userService, ChannelService channelService) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("./messages.ser"))) {
            data = (Map<UUID, Message>) ois.readObject();
        }catch (IOException | ClassNotFoundException e) {
            System.out.println(e.getMessage());
            data = new HashMap<>();
        }
        saveData();
        this.userService = userService;
        this.channelService = channelService;
    }

    @Override
    public Message createMessage(UUID userId, String content, UUID channelId) {
        loadData();
        Validator.validateNotNull(userId, "메시지 생성 시 userId가 null일 수 없음");
        Validator.validateNotNull(content, "메시지 생성 시 content가 null일 수 없음");
        Validator.validateNotNull(channelId, "메시지 생성 시 channelId가 null일 수 없음");
        Validator.validateNotBlank(content,"메시지 생성 시 content가 빈문자열일 수 없음");
        User user = userService.findById(userId);
        Channel channel = channelService.findById(channelId);
        if (!user.isInChannel(channel)) {
            throw new IllegalStateException("해당 채널에 참여 중이 아니므로 메시지를 작성할 수 없음");
        }
        Message message = new Message(user, content, channel);
        user.addMessage(message, channel);
        data.put(message.getId(), message);
        saveData();
        return message;
    }

    @Override
    public Message findById(UUID id) {
        loadData();
        Message message = data.get(id);
        if (message == null) {
            throw new IllegalStateException("해당 id의 메시지를 찾을 수 없음");
        }
        return message;
    }

    @Override
    public List<Message> findAll() {
        loadData();
        return new ArrayList<>(data.values());
    }

    @Override
    public Message updateById(UUID id, String content) {
        loadData();
        Message targetMessage = findById(id);
        Validator.validateNotNull(content, "업데이트하려는 메시지 내용이 null일 수 없음");
        Validator.validateNotBlank(content, "업데이트하려는 메시지 내용이 빈내용일 수 없음");
        targetMessage.updateContent(content);
        saveData();
        return targetMessage;
    }

    @Override
    public void deleteById(UUID id) {
        loadData();
        Message message = findById(id);
        Channel channel = message.getChannel();
        User user = message.getUser();

        channel.removeMessage(message);
        user.removeMessage(message, channel);
        data.remove(id);
        saveData();
    }

    @Override
    public List<Message> getMessagesByUserId(UUID userId) {
        loadData();
        User user = userService.findById(userId);
        return data.values().stream()
                .filter(message -> message.getUser() == user)
                .toList();
    }

    @Override
    public List<Message> getMessagesByChannelId(UUID channelId) {
        loadData();
        Channel channel = channelService.findById(channelId);
        return data.values().stream()
                .filter(message -> message.getChannel()==channel)
                .toList();
    }

    public void loadData() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("./messages.ser"))) {
            data = (Map<UUID, Message>) ois.readObject();
        }catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void saveData() {
        try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("./messages.ser"))){
            oos.writeObject(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
