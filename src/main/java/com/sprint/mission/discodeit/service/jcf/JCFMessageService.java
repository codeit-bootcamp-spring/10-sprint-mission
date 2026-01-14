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
        User author = userService.getUser(userId);

        validateMessageContent(content);

        Message message = new Message(channel, author, content);
        data.put(message.getId(), message);
        channelService.enterChannel(author.getId(), channel.getId());
        return message;
    }

    @Override
    public Message getMessage(UUID id) {
        validateMessageId(id);
        return data.get(id);
    }

    public List<Message> getMessagesByChannel(UUID channelId) {
        return data.values().stream()
                .filter(m -> m.getChannel().getId().equals(channelId))
                .toList();
    }
    @Override
    public List<Message> getAllMessages() {
        return new ArrayList<>(data.values());
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
        validateMessageId(id);
        data.remove(id);
    }

    private void validateMessageContent(String content) {
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("내용을 다시 입력해주세요");
        }
    }

    private void validateMessageId(UUID id){
        if(id == null) throw new IllegalArgumentException("메시지 ID가 없습니다.");
        if(!data.containsKey(id)) throw new IllegalArgumentException("존재하지 않는 메시지입니다.");
    }
}
