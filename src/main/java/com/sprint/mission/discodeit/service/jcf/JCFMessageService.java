package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.validation.ValidationMethods;

import java.util.*;

public class JCFMessageService implements MessageService {
//    private final Map<UUID, Message> data;
    private final MessageRepository messageRepository;
    private final UserService userService;
    private final ChannelService channelService;

    public JCFMessageService(MessageRepository messageRepository, UserService userService, ChannelService channelService) {
        this.messageRepository = messageRepository;
        this.userService = userService;
        this.channelService = channelService;
    }

    // C. 생성(메세지 작성)
    @Override
    public Message createMessage(UUID channelId, UUID authorId, String content) {
        // 로그인 되어있는 user ID null / user 객체 존재 확인
        User author = validateAndGetUserByUserId(authorId);
        // Channel ID null & channel 객체 존재 확인
        Channel channel = validateAndGetChannelByChannelId(channelId);
        // String `null` or `blank` 검증
        ValidationMethods.validateNullBlankString(content, "content");

        // author의 channel 참여 여부 확인
        if (!channel.getChannelMembersList().stream()
                .anyMatch(user -> user.getId().equals(authorId))) {
            throw new IllegalArgumentException("현재 author은 해당 channel에 참가하지 않았습니다.");
        }

        Message message = new Message(channel, author, content);

        messageRepository.save(message);
        linkMessage(author, channel, message);
        return message;
    }

    // R. 읽기
    // 특정 메시지 정보 읽기 by messageId
    @Override
    public Optional<Message> findMessageById(UUID messageId) {
        // Message ID `null` 검증
        ValidationMethods.validateId(messageId);

        return messageRepository.findById(messageId);
    }

    // R. 모두 읽기
    // 메시지 전체
    @Override
    public List<Message> findAllMessages() {
        return messageRepository.findAll();
    }

    // 특정 채널의 모든 메시지 읽어오기
    @Override
    public List<Message> findChannelMessagesByChannelId(UUID channelId) {
        // Channel ID null & channel 객체 존재 확인
        validateChannelByChannelId(channelId);

        return findAllMessages().stream()
                .filter(message -> message.getMessageChannel().getId().equals(channelId))
                .toList();
    }

    // 특정 사용자가 작성한 모든 메시지
    @Override
    public List<Message> findUserMessagesByUserId(UUID userId) {
        // 로그인 되어있는 user ID null / user 객체 존재 확인
        validateUserByUserId(userId);

        return findAllMessages().stream()
                .filter(message -> message.getAuthor().getId().equals(userId))
                .toList();
    }

    // U. 수정
    // 메시지 수정
    @Override
    public Message updateMessageContent(UUID requestUserId, UUID messageId, String content) {
        // Message ID null & Message 객체 존재 확인
        Message message = validateAndGetMessageByMessageId(messageId);
        // requestUser가 해당 message를 작성한 게 맞는지 확인
        verifyMessageAuthor(message, requestUserId);

        // content `null` or `blank` 검증
        ValidationMethods.validateNullBlankString(content, "content");

        message.updateContent(content);
        return message;
    }

    // D. 삭제
    @Override
    public void deleteMessage(UUID userId, UUID messageId) {
        // 요청자의 user ID null / user 객체 존재 확인
        validateUserByUserId(userId);
        // Message ID null & Message 객체 존재 확인
        Message message = validateAndGetMessageByMessageId(messageId);
        // Channel ID null & channel 객체 존재 확인
        Channel channel = validateAndGetChannelByChannelId(message.getMessageChannel().getId());

        // message author의 id와 삭제 요청한 user id가 동일한지 확인하고
        // 메세지가 작성된 channel의 owner와 user가 동일한지 확인해서 동일하지 않다면 exception
        if (!message.getAuthor().getId().equals(userId) && !channel.getOwner().getId().equals(userId)) {
            throw new IllegalStateException("권한이 없습니다.");
        }

        User author = validateAndGetUserByUserId(message.getAuthor().getId());

        unlinkMessage(author, channel, message);
        messageRepository.delete(messageId);
    }

    public void linkMessage(User author, Channel channel, Message message) {
        // author(user)의 writeMessageList에 message 객체 저장
        author.writeMessage(message);
        // channel의 channelMessagesList에 message 객체 저장
        channel.addMessage(message);
    }
    public void unlinkMessage(User author, Channel channel, Message message) {
        // author(user)의 writeMessageList에 저장된 message 객체 삭제
        author.removeUserMessage(message.getId());
        // channel의 channelMessagesList에 저장된 message 객체 삭제
        channel.removeMessageInChannel(message.getId());
    }

    //// validation
    // 로그인 되어있는 user ID null & user 객체 존재 확인
    public void validateUserByUserId(UUID userId) {
        userService.findUserById(userId)
                .orElseThrow(() -> new NoSuchElementException("해당 사용자가 없습니다."));
    }
    public User validateAndGetUserByUserId(UUID userId) {
        return userService.findUserById(userId)
                .orElseThrow(() -> new NoSuchElementException("해당 사용자가 없습니다."));
    }

    // Channel ID null & channel 객체 존재 확인
    public Channel validateAndGetChannelByChannelId(UUID channelId) {
        return channelService.findChannelById(channelId)
                .orElseThrow(() -> new NoSuchElementException("해당 채널이 없습니다."));
    }
    public void validateChannelByChannelId(UUID channelId) {
        channelService.findChannelById(channelId)
                .orElseThrow(() -> new NoSuchElementException("해당 채널이 없습니다."));
    }

    // Message ID null & channel 객체 존재 확인
    public Message validateAndGetMessageByMessageId(UUID messageId) {
        return findMessageById(messageId)
                .orElseThrow(() -> new NoSuchElementException("해당 메세지가 없습니다."));
    }

    // message의 author와 삭제 요청한 user가 동일한지
    public void verifyMessageAuthor(Message message, UUID userId) {
        // message author의 id와 삭제 요청한 user id가 동일한지 확인
        if (!message.getAuthor().getId().equals(userId)) {
            throw new IllegalStateException("본인이 작성한 메세지만 수정 가능합니다.");
        }
    }
}
