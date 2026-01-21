package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.List;
import java.util.UUID;

public class BasicMessageService implements MessageService {

    private final MessageRepository messageRepository;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;

    public BasicMessageService(MessageRepository messageRepository, ChannelRepository channelRepository,
                               UserRepository userRepository) {
        this.messageRepository = messageRepository;
        this.channelRepository = channelRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Message createMessage(String content, UUID channelId, UUID userId) {
        Channel channel = channelRepository.findById(channelId);
        User user = userRepository.findById(userId);

        if(channel == null) throw new IllegalArgumentException("채널을 찾을 수 없습니다.");
        if(user == null) throw new IllegalArgumentException("유저를 찾을 수 없습니다.");

        Message newMessage = new Message(content, channel, user);

        user.addMessage(newMessage);
        channel.addMessage(newMessage);

        messageRepository.save(newMessage);
        channelRepository.save(channel);
        userRepository.save(user);

        return newMessage;
    }

    @Override
    public void deleteMessage(UUID id) {
        Message targetMessage = findMessageById(id);
        User user = userRepository.findById(targetMessage.getUser().getId());
        Channel channel = channelRepository.findById(targetMessage.getChannel().getId());

        if (user != null) {
            user.getMyMessages().remove(targetMessage);
            userRepository.save(user);
        }
        if (channel != null) {
            channel.getChannelMessages().remove(targetMessage);
            channelRepository.save(channel);
        }

        messageRepository.delete(id);
        System.out.println("메시지 삭제 완료: " + targetMessage.getContent());
    }

    @Override
    public Message findMessageById(UUID id) {
        Message message = messageRepository.findById(id);
        if (message == null) {
            throw new IllegalArgumentException("해당 메시지가 없습니다.");
        }
        return message;
    }

    @Override
    public Message updateMessage(UUID id, String newContent) {
        Message targetMessage = findMessageById(id);
        targetMessage.updateContent(newContent);
        messageRepository.save(targetMessage);
        System.out.println("메시지가 수정되었습니다");
        return targetMessage;
    }

    @Override
    public List<Message> findAllMessages() {
        return messageRepository.findAll();
    }

    @Override
    public List<Message> findMessagesByChannelId(UUID channelId) {
        return messageRepository.findByChannelId(channelId);
    }

    @Override
    public List<Message> findMessagesByUserId(UUID userId) {
        return messageRepository.findByUserId(userId);
    }
}
