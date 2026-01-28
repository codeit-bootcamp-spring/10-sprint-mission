package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.util.Validator;

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
        Validator.validateNotNull(content, "업데이트하려는 메시지 내용이 null일 수 없음");
        Validator.validateNotBlank(content, "업데이트하려는 메시지 내용이 빈내용일 수 없음");
        targetMessage.updateContent(content);
        return targetMessage;
    }

    @Override
    public void deleteById(UUID id) {
        Message message = findById(id);
        Channel channel = message.getChannel();
        User user = message.getUser();

        channel.removeMessage(message);
        user.removeMessage(message, channel);
        messages.remove(id);
    }
    
    // 해당 user Id를 가진 유저가 작성한 메시지 목록을 반환
    @Override
    public List<Message> getMessagesByUserId(UUID userId) {
        User user = userService.findById(userId);
        return messages.values().stream()
                .filter(message -> message.getUser().equals(user))
                .toList();
    }

    // 해당 channel Id를 가진 채널의 메시지 목록을 반환
    @Override
    public List<Message> getMessagesByChannelId(UUID channelId) {
        Channel channel = channelService.findById(channelId);
        return messages.values().stream()
                .filter(message -> message.getChannel().equals(channel))
                .toList();
    }

    @Override
    public void loadData() {

    }

    @Override
    public void saveData() {

    }
}
