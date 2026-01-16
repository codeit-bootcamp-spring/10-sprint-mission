package com.sprint.mission.discodeit.service.jcf;


import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.MessageNotFoundException;
import com.sprint.mission.discodeit.exception.UserNotFoundException;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import java.util.*;

public class JCFMessageService implements MessageService {

    private final Map<UUID,Message> messages = new LinkedHashMap<>();
    private final UserService userService;

    public JCFMessageService(UserService userService) {
        this.userService = userService;
    }

    public UserService getUserService() {
        return this.userService;
    }

    // 메시지 생성
    public Message createMessage(User sender, Channel channel, String content) {
        if (sender == null) {
            throw new UserNotFoundException();
        }
        if (channel == null) {
            throw new ChannelNotFoundException();
        }
        Message message = new Message(sender, channel, content);
        messages.put(message.getId(), message);
        return message;
    }

    // 단건 조회
    public Message findMessage(UUID messageId) {
        Message message = messages.get(messageId);
        if (message == null) {
            throw new MessageNotFoundException();
        }
        return message;
    }

    // 채널별 메시지 조회
    public List<Message> findAllByChannelMessage(UUID channelId) {
        return messages.values().stream()
                .filter(m -> m.getChannel().getId().equals(channelId))
                .toList();
    }

    // 전체 메시지 조회
    public List<Message> findAllMessage() {
        return new ArrayList<>(messages.values());
    }

    // 메시지 수정
    public Message updateMessage(UUID messageId, String newContent) {
        Message message = findMessage(messageId);
        message.updateContent(newContent);
        return message;
    }

    // 메시지 삭제
    public void deleteMessage(UUID messageId) {
        if (messages.remove(messageId) == null) {
            throw new MessageNotFoundException();
        }
    }
}
