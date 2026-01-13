package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFMessageService implements MessageService {
    private final Map<UUID, Message> messages;
    private final UserService userService;
    private final ChannelService channelService;

    public JCFMessageService(UserService userService, ChannelService channelService) {
        messages = new HashMap<>();
        this.userService = userService;
        this.channelService = channelService;
    }

    // 외부에서 객체를 받는 것 보다는 메소드 내부에서 객체 생성해서 반환
    @Override
    public Message createMessage(UUID userId, String content, UUID channelId) {
        if (userId == null || content == null || channelId == null) {
            throw new IllegalArgumentException("메시지 생성 시 userId, content, channelId이 null일 수 없음");
        }
        if (content.isEmpty()) {
            throw new IllegalArgumentException("메시지 생성 시 content가 빈문자열일 수 없음");
        }
        User user = userService.findById(userId);
        Channel channel = channelService.findById(channelId);
        if (!user.isInChannel(channel)) {
            throw new IllegalStateException("해당 채널에 참여 중이 아니므로 메시지를 작성할 수 없음");
        }
        Message message = new Message(user, content, channel);
        user.addMessage(message, channel);
        messages.put(message.getId(), message);
        return message;
    }

    @Override
    public Message findById(UUID id) {
        Message message = messages.get(id);
        if (message == null) {
            throw new IllegalStateException("해당 id의 메시지를 찾을 수 없음");
        }
        return message;
    }

    @Override
    public List<Message> findAll() {
        return new ArrayList<>(messages.values());
    }

    @Override
    public Message updateById(UUID id, String content) {
        Message targetMessage = findById(id);
        if (content == null) {
            throw new IllegalArgumentException("업데이트하려는 메시지 내용이 null일 수 없음");
        }
        targetMessage.updateContent(content);
        return targetMessage;
    }

    @Override
    public void deleteById(UUID id) {
        findById(id);
        messages.remove(id);
    }
}
