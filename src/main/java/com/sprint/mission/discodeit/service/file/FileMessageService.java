package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.consistency.FileConsistencyManager;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.UUID;

public class FileMessageService implements MessageService {
    private final FileConsistencyManager fileConsistencyManager;
    private final MessageRepository messageRepository;
    private final ChannelService channelService;
    private final UserService userService;

    public FileMessageService(FileConsistencyManager fileConsistencyManager,
                              MessageRepository messageRepository,
                              ChannelService channelService,
                              UserService userService) {
        this.fileConsistencyManager = fileConsistencyManager;
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
        if (!channel.getUsers().contains(user)) {
            throw new RuntimeException("채널에 가입되어 있지 않습니다.");
        }

        // 메시지 생성
        Message message = new Message(channel, user, content);
        // 메시지를 소유해야 하는 채널과 유저의 메시지 목록에 추가
        message.addToChannelAndUser();
        // 메시지 추가
        return fileConsistencyManager.saveMessage(message);
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
        // 존재하는 채널인지 검색 및 검증
        Channel channel = channelService.findChannelById(channelId);
        // 존재하는 유저인지 검색 및 검증
        User user = userService.findUserById(userId);

        // 메시지 검색 및 권한 확인
        Message message = validateMessageAccess(channel, user, messageId);
        // 메시지 내용 수정
        message.updateMessageContent(newContent);
        // 수정 내용 반영
        return fileConsistencyManager.saveMessage(message);
    }

    @Override
    public void deleteMessage(UUID channelId, UUID userId, UUID messageId) {
        // 존재하는 채널인지 검색 및 검증
        Channel channel = channelService.findChannelById(channelId);
        // 존재하는 유저인지 검색 및 검증
        User user = userService.findUserById(userId);

        // 메시지 검색 및 권한 확인
        Message message = validateMessageAccess(channel, user, messageId);
        // 메시지를 소유하고 있는 채널과 유저의 메시지 목록에서 제거
        message.removeFromChannelAndUser();
        // 메시지 삭제 및 삭제 내용 반영
        fileConsistencyManager.deleteMessage(message);
    }

    private Message validateMessageAccess(Channel channel, User user, UUID messageId) {
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
        User writer = message.getUser();
        if (!writer.equals(user)) {
            throw new RuntimeException("해당 메시지에 대한 권한이 없습니다.");
        }

        return message;
    }
}
