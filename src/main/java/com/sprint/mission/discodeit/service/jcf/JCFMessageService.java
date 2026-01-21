package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.UUID;

public class JCFMessageService implements MessageService {
    private final MessageRepository messageRepository;
    private final ChannelService channelService;
    private final UserService userService;

    public JCFMessageService(MessageRepository messageRepository,
                             ChannelService channelService,
                             UserService userService) {
        this.messageRepository = messageRepository;
        this.channelService = channelService;
        this.userService = userService;
    }

    @Override
    public Message createMessage(UUID channelId, UUID userId, String content) {
        // 존재하는 채널과 유저인지 검색 및 검증
        Channel channel = channelService.findChannelById(channelId);
        User user = userService.findUserById(userId);

        // 채널에 가입된 유저만 메시지 작성 가능
        List<Channel> joinedChannels = user.getChannels();
        if (!joinedChannels.contains(channel)) {
            throw new RuntimeException("채널에 가입되어 있지 않습니다.");
        }

        // 메시지 생성 및 추가
        Message message = new Message(channel, user, content);
        messageRepository.saveMessage(message);
        // 채널과 유저가 가지고 있는 메시지 목록에 추가
        message.addToChannelAndUser();

        return message;
    }

    @Override
    public List<String> readMessagesByChannelId(UUID channelId) {
        // 메시지를 조회하려는 채널이 존재하는지 검증
        Channel channel = channelService.findChannelById(channelId);

        // 해당 채널의 모든 메시지를 반환
        return channel.getMessages()
                .stream()
                .map(Message::formatForDisplay)
                .toList();
    }

    @Override
    public List<String> readMessagesByUserId(UUID userId) {
        // 메시지를 조회하려는 유저가 존재하는지 검증
        User user = userService.findUserById(userId);

        // 해당 유저가 작성한 모든 메시지를 반환
        return user.getMessages()
                .stream()
                .map(Message::formatForDisplay)
                .toList();
    }

    @Override
    public List<Message> findMessagesByChannelId(UUID channelId) {
        // 메시지를 조회하려는 채널이 존재하는지 검증
        Channel channel = channelService.findChannelById(channelId);

        // 해당 채널의 모든 메시지 정보를 반환
        return channel.getMessages();
    }

    @Override
    public Message findMessageByChannelIdAndMessageId(UUID channelId, UUID messageId) {
        // 존재하는 채널인지 검색 및 검증
        Channel channel = channelService.findChannelById(channelId);

        // 메시지가 존재하지 않을 경우 예외 발생
        Message message = messageRepository.findMessageByMessageId(messageId);
        if (message == null) {
            throw new RuntimeException("메시지가 존재하지 않습니다.");
        }

        // 채널이 맞지 않을 경우 예외 발생
        if (!message.getChannel().equals(channel)) {
            throw new RuntimeException("메시지가 존재하지 않습니다.");
        }

        return message;
    }

    @Override
    public Message updateMessageContent(UUID channelId, UUID userId, UUID messageId, String newContent) {
        // 메시지 검색 및 권한 확인
        Message message = validateMessageAccess(channelId, userId, messageId);
        // 메시지 내용 수정
        return message.updateMessageContent(newContent);
    }

    @Override
    public void deleteMessage(UUID channelId, UUID userId, UUID messageId) {
        // 메시지 검색 및 권한 확인
        Message message = validateMessageAccess(channelId, userId, messageId);

        // 채널과 유저가 가지고 있는 메시지 목록에서 삭제
        message.removeFromChannelAndUser();
        // 메시지 삭제
        messageRepository.deleteMessage(messageId);
    }

    private Message validateMessageAccess(UUID channelId, UUID userId, UUID messageId) {
        // 존재하는 채널인지 검색 및 검증
        Channel channel = channelService.findChannelById(channelId);
        // 존재하는 유저인지 검색 및 검증
        User user = userService.findUserById(userId);

        // 채널에 가입된 유저인지 확인
        List<Channel> joinedChannels = user.getChannels();
        if (!joinedChannels.contains(channel)) {
            throw new RuntimeException("채널에 가입되어 있지 않습니다.");
        }

        // 메시지가 존재하지 않을 경우 예외 발생
        Message message = messageRepository.findMessageByMessageId(messageId);
        if (message == null) {
            throw new RuntimeException("메시지가 존재하지 않습니다.");
        }

        // 채널이 맞지 않을 경우 예외 발생
        if (!message.getChannel().equals(channel)) {
            throw new RuntimeException("메시지가 존재하지 않습니다.");
        }

        // 작성자가 아닐 경우 예외 발생
        UUID writerId = message.getUser().getId();
        if (!writerId.equals(userId)) {
            throw new RuntimeException("해당 메시지에 대한 권한이 없습니다.");
        }

        return message;
    }
}
