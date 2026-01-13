package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.util.Validators;

import java.util.*;

public class JCFMessageService implements MessageService {
    final ArrayList<Message> list;
    private final ChannelService channelService;
    private final UserService userService;

    public JCFMessageService(UserService userService, ChannelService channelService) {
        this.list = new ArrayList<>();
        this.userService = userService;
        this.channelService = channelService;
    }

    @Override
    public Message createMessage(String content, UUID channelId, UUID userId) {
        Validators.validationMessage(content);
        Objects.requireNonNull(channelId, "channelId는 null이 될 수 없습니다.");
        Objects.requireNonNull(userId, "userId는 null이 될 수 없습니다.");

        Channel channel = channelService.validateExistenceChannel(channelId);
        User user = userService.validateExistenceUser(userId);
        Message message = new Message(content, channel, user);

        list.add(message);
        return message;
    }

    @Override
    public Message readMessage(UUID id) {
        Objects.requireNonNull(id, "id는 null이 될 수 없습니다.");
        for (Message message : list) {
            if (id.equals(message.getId())){
                return message;
            }
        }
        return null;
    }

    @Override
    public List<Message> readAllMessage() {
        return list;
    }

    @Override
    public void updateMessage(UUID id, String content) {
        Objects.requireNonNull(id, "id는 null이 될 수 없습니다.");
        Validators.validationMessage(content);
        Message message = validateExistenceMessage(id);
        message.updateContent(content);
    }

    public void deleteMessage(UUID id) {
        Objects.requireNonNull(id, "id는 null이 될 수 없습니다.");
        Message message = validateExistenceMessage(id);
        list.remove(message);
    }

    public boolean isMessageDeleted(UUID id) {
        Objects.requireNonNull(id, "id는 null이 될 수 없습니다.");
        for (Message message : list) {
            if(id.equals(message.getId())) {
                return false;
            }
        }
        return true;
    }


    public Message validateExistenceMessage(UUID id) {
        Message message = readMessage(id);
        if(message == null) {
            throw new NoSuchElementException("메세지 id가 존재하지 않습니다.");
        }
        return message;
    }

}
