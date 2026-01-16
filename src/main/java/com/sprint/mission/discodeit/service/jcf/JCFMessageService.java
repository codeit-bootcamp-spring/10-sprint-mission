package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.validation.ValidationMethods;

import java.util.*;

public class JCFMessageService implements MessageService {
    private final Map<UUID, Message> data;
    private final UserService userService;
    private final ChannelService channelService;

    public JCFMessageService(UserService userService, ChannelService channelService) {
        this.data = new HashMap<>();
        this.userService = userService;
        this.channelService = channelService;
    }

    @Override
    public String toString() {
        return "JCFMessageService{" +
//                "data = " + data + ", " +
                "data key = " + data.keySet() + ", " +
                "data value = " + data.values() + ", " +
                "data size = " + data.size() + ", " +
                '}';
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
        if (!channel.getChannelMembersIds().contains(authorId)) {
            throw new IllegalArgumentException("현재 author은 해당 channel에 참가하지 않았습니다.");
        }

        Message message = new Message(channelId, authorId, content);
        data.put(message.getId(), message);
        author.writeMessage(message.getId());
        channel.addMessage(message.getId());
        return message;
    }

    // R. 읽기
    // 특정 메시지 정보 읽기 by messageId
    @Override
    public Optional<Message> findMessageById(UUID messageId) {
        // Message ID `null` 검증
        ValidationMethods.validateId(messageId);

        return Optional.ofNullable(data.get(messageId));
    }

    // R. 모두 읽기
    // 메시지 전체
    @Override
    public List<Message> findAllMessages() {
        return new ArrayList<>(data.values());
    }

    // 특정 채널의 모든 메시지 읽어오기
    @Override
    public List<Message> findChannelMessagesByChannelId(UUID channelId) {
        // Channel ID null & channel 객체 존재 확인
        validateChannelByChannelId(channelId);

        return findAllMessages().stream()
                .filter(message -> message.getMessageChannelId().equals(channelId))
                .toList();
    }

    // 특정 사용자가 작성한 모든 메시지
    @Override
    public List<Message> findUserMessagesByUserId(UUID userId) {
        // 로그인 되어있는 user ID null / user 객체 존재 확인
        User user = validateAndGetUserByUserId(userId);

        return findAllMessages().stream()
                .filter(message -> message.getAuthorId().equals(userId))
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
        Channel channel = validateAndGetChannelByChannelId(message.getMessageChannelId());

        // message author의 id와 삭제 요청한 user id가 동일한지 확인하고
        // 메세지가 작성된 channel의 owner와 user가 동일한지 확인해서 동일하지 않다면 exception
        if (!message.getAuthorId().equals(userId) && !channel.getOwnerId().equals(userId)) {
            throw new IllegalStateException("권한이 없습니다.");
        }

        User author = validateAndGetUserByUserId(message.getAuthorId());

        // author 객체에 저장된 message ids 삭제
        author.removeUserMessage(messageId);
        // channel 객체에 저장된 message ids 삭제
        channel.removeMessageInChannel(messageId);
        data.remove(messageId);
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
        if (!message.getAuthorId().equals(userId)) {
            throw new IllegalStateException("본인이 작성한 메세지만 수정 가능합니다.");
        }
    }
}
