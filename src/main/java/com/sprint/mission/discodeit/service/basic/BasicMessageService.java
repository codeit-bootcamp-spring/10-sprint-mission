package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.factory.ServiceType;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFChannelRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFMessageRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFUserRepository;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.List;
import java.util.UUID;

public class BasicMessageService implements MessageService {
    private static BasicMessageService instance;

    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;

    private BasicMessageService(ServiceType type) {
        if (type == ServiceType.JCF) {
            userRepository = JCFUserRepository.getInstance();
            channelRepository = JCFChannelRepository.getInstance();
            messageRepository = JCFMessageRepository.getInstance();
        } else {
            userRepository = FileUserRepository.getInstance();
            channelRepository = FileChannelRepository.getInstance();
            messageRepository = FileMessageRepository.getInstance();
        }
    }

    public static MessageService getInstance(ServiceType type) {
        if (instance == null) instance = new BasicMessageService(type);
        return instance;
    }

    // data 필드를 활용해 생성, 조회, 수정, 삭제하는 메소드를 구현하세요.
    @Override
    public Message create(UUID userId, String text, UUID channelId) throws RuntimeException {
        // 메시지를 생성 전, 유저가 해당 채널에 속해있는지 확인한다.
        Channel channel = channelRepository.findById(channelId);
        User user = userRepository.findById(userId);

        if (user.getChannelList().stream()
            .noneMatch(ch -> ch.getId().equals(channelId))) {
            throw new IllegalArgumentException(
                user.getUserName() + "님은 " + channel.getName() + " 채널에 속해있지 않아 메시지를 보낼 수 없습니다."
            );
        }

        Message newMessage = new Message(user, text, channel);
        messageRepository.save(newMessage);

        // channel과 user의 messageList에 현재 message를 add
        channel.addMessage(newMessage);
        channelRepository.save(channel);

        user.addMessage(newMessage);
        userRepository.save(user);

        return newMessage;
    }

    @Override
    public Message findById(UUID id) {
        return messageRepository.findById(id);
    }

    @Override
    public List<Message> findByUser(UUID userId) {
        return userRepository.findById(userId).getMessageList();
    }


    @Override
    public List<Message> findByChannel(UUID channelId) {
        return channelRepository.findById(channelId).getMessageList();
    }

    @Override
    public Message updateById(UUID messageId, String text) {
        Message message = messageRepository.findById(messageId);

        message.updateText(text);
        message.updateUpdatedAt(System.currentTimeMillis());
        messageRepository.save(message);

        return message;
    }

    @Override
    public void delete(UUID messageId) {
        Message message = messageRepository.findById(messageId);

        // 유저쪽에서 메시지 정보 삭제
        User user = userRepository.findById(message.getSender().getId());
        user.getMessageList()
            .removeIf(msg -> msg.getId().equals(messageId));
        userRepository.save(user);

        // 채널쪽에 메시지 정보 삭제
        Channel channel = channelRepository.findById(message.getChannel().getId());
        channel.getMessageList()
            .removeIf(msg -> msg.getId().equals(messageId));
        channelRepository.save(channel);

        // 실제 메시지 객체 삭제
        messageRepository.delete(messageId);
    }
}
