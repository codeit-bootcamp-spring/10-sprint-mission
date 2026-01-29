package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFMessageService implements MessageService {
    private final Map<UUID, Message> data;
    private final ChannelService channelService;
    private final UserService userService;
    public JCFMessageService(JCFUserService userService, JCFChannelService channelService) {
        this.data = new HashMap<>();
        this.userService = userService;
        this.channelService = channelService;
    }

    @Override
    public Message createMessage(UUID channelId, UUID userId, String content) {
        Channel channel = channelService.getChannel(channelId);
        User user = userService.getUser(userId);

        if (!channel.getUsers().contains(user)) {
            throw new IllegalArgumentException("채널에 먼저 입장해야 메시지를 남길 수 있습니다.");
        }

        validateMessageContent(content);

        Message message = new Message(channel, user, content);
        data.put(message.getId(), message);

        channel.addMessage(message);
        user.addMessage(message);

        return message;
    }

    @Override
    public Message getMessage(UUID id) {
        return validateMessageId(id);
    }

    public List<Message> getMessagesByChannel(UUID channelId) {
        return data.values().stream()
                .filter(m -> m.getChannelId().equals(channelId))
                .toList();
    }
    @Override
    public List<Message> getAllMessages() {
        return new ArrayList<>(data.values());
    }

    @Override
    public List<Message> getMessagesByChannelId(UUID channelId) {
        return new ArrayList<>(channelService.getChannel(channelId).getMessages());
    }

    @Override
    public List<Message> getMessagesByUserId(UUID userId) {
        return new ArrayList<>(userService.getUser(userId).getMessages());
    }

    @Override
    public Message updateMessage(String content, UUID id) {
        validateMessageId(id);
        Message message = data.get(id);
        Optional.ofNullable(content)
                .filter(c -> !c.isBlank())
                .ifPresent(message::updateContent);
        return message;
    }

    @Override
    public void deleteMessage(UUID id) {
        Message message = validateMessageId(id);

        message.getChannel().removeMessage(message);
        message.getUser().removeMessage(message);

        data.remove(id);
    }

    private void validateMessageContent(String content) {
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("내용을 다시 입력해주세요");
        }
    }

    private Message validateMessageId(UUID id){
        return Optional.ofNullable(data.get(id))
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 메시지입니다"));
    }
}
