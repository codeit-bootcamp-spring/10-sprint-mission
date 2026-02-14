package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BasicMessageService implements MessageService {
    private final MessageRepository messageRepository;

    private final UserService userService;
    private final ChannelService channelService;

    public BasicMessageService(MessageRepository messageRepository, UserService userService, ChannelService channelService) {
        this.messageRepository = messageRepository;
        this.userService = userService;
        this.channelService = channelService;
    }

    @Override
    public void save(Message message) {
        messageRepository.save(message);
    }

    @Override
    public Message create(UUID userId, UUID channelId, String content) {
        User user = userService.findById(userId);
        Channel channel = channelService.findById(channelId);

        if (!channel.getMembers().contains(user)) {
            throw new IllegalStateException("해당 채널에 속해 있지 않으므로 메세지를 보낼 수 없습니다.");
        }

        Message message = new Message(user, channel, content);
        Message savedMessage = messageRepository.save(message);

        userService.save(user);
        channelService.save(channel);

        return savedMessage;
    }

    @Override
    public Message findById(UUID id) {
        return messageRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 메세지 입니다. ID: " + id));
    }

    @Override
    public List<Message> findAll() {
        return messageRepository.findAll();
    }

    @Override
    public List<Message> findMessagesByChannelId(UUID channelId) {
        return channelService.findById(channelId).getMessages();
    }

    @Override
    public List<Message> findMessagesByUserId(UUID userId) {
        return userService.findById(userId).getMyMessages();
    }

    @Override
    public Message update(UUID id, String content) {
        Message updateContent = findById(id);
        updateContent.updateMessage(content);
        return messageRepository.save(updateContent);
    }

    @Override
    public void deleteMessageByMessageId(UUID messageId) {
        Message message = findById(messageId);
        User user = message.getUser();
        Channel channel = message.getChannel();
        if (message.getUser() != null) {
            message.getUser().removeMyMessages(message);
            userService.save(user);
        }
        if (message.getChannel() != null) {
            message.getChannel().removeMessages(message);
            channelService.save(channel);
        }

        // 실제 데이터 파기
        messageRepository.deleteById(messageId);
    }

    @Override
    public void deleteMessagesByUserId(UUID userId) {
        new ArrayList<> (findMessagesByUserId(userId)).forEach(m-> deleteMessageByMessageId(m.getId()));
    }

    @Override
    public void deleteMessagesByChannelId(UUID channelId) {
        new ArrayList<> (findMessagesByChannelId(channelId)).forEach(m-> deleteMessageByMessageId(m.getId()));
    }
}
