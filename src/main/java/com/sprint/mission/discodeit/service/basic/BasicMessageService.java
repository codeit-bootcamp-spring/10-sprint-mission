package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.validation.ValidationMethods;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public Message createMessage(MessageCreateRequest request) {
        // 로그인 되어있는 user ID null / user 객체 존재 확인
        User author = validateAndGetUserByUserId(request.authorId());
        // Channel ID null & channel 객체 존재 확인
        Channel channel = validateAndGetChannelByChannelId(request.channelId());
        // String `null` or `blank` 검증
        ValidationMethods.validateNullBlankString(request.content(), "content");

        // author의 channel 참여 여부 확인
        if (!channel.getChannelMembersList().stream()
                .anyMatch(user -> user.getId().equals(request.authorId()))) {
            throw new IllegalArgumentException("현재 author은 해당 channel에 참가하지 않았습니다.");
        }

        Message message = new Message(channel, author, request.content());

        if (request.attachments() != null) {
            for (BinaryContentCreateRequest attachment : request.attachments()) {
                if (attachment == null) continue;

                byte[] attachmentContent = attachment.binaryContent();

                if (attachmentContent == null || attachmentContent.length == 0) continue;
                BinaryContent binaryContent = new BinaryContent(attachmentContent);
                message.addAttachmentId(binaryContent.getId());
                binaryContentRepository.save(binaryContent);
            }
        }
        linkMessage(author, channel, message);
        messageRepository.save(message);
        channelRepository.save(channel);
        userRepository.save(author);
        return message;
    }

    @Override
    public Message findMessageById(UUID messageId) {
        // Message ID `null` 검증
        ValidationMethods.validateId(messageId);

        return messageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException("해당 메세지가 없습니다."));
    }

    @Override
    public List<Message> findAllMessages() {
        return messageRepository.findAll();
    }

    @Override
    public List<Message> findAllByChannelId(UUID channelId) {
        // Channel ID null & channel 객체 존재 확인
        validateChannelByChannelId(channelId);

        return messageRepository.findByChannelId(channelId);
    }

    @Override
    public List<Message> findUserMessagesByUserId(UUID userId) {
        // 로그인 되어있는 user ID null / user 객체 존재 확인
        validateUserByUserId(userId);

        return messageRepository.findByAuthorId(userId);
    }

    @Override
    public Message updateMessageContent(MessageUpdateRequest messageUpdateRequest) {
        // Message ID null & Message 객체 존재 확인
        Message message = validateAndGetMessageByMessageId(messageUpdateRequest.messageId());
        // requestUser가 해당 message를 작성한 게 맞는지 확인
        verifyMessageAuthor(message, messageUpdateRequest.requestUserId());

        // content `null` or `blank` 검증
        ValidationMethods.validateNullBlankString(messageUpdateRequest.content(), "content");

        message.updateContent(messageUpdateRequest.content());
        messageRepository.save(message);
        return message;
    }

    @Override
    public void deleteMessage(UUID userId, UUID messageId) {
        // 요청자의 user ID null / user 객체 존재 확인
        validateUserByUserId(userId);
        // Message ID null & Message 객체 존재 확인
        Message message = validateAndGetMessageByMessageId(messageId);
        // Channel ID null & channel 객체 존재 확인
        Channel channel = validateAndGetChannelByChannelId(message.getChannel().getId());

        // message author의 id와 삭제 요청한 user id가 동일한지 확인하고
        // 메세지가 작성된 channel의 owner와 user가 동일한지 확인해서 동일하지 않다면 exception
        if (!message.getAuthor().getId().equals(userId) && !channel.getOwner().getId().equals(userId)) {
            throw new IllegalStateException("권한이 없습니다.");
        }

        User author = validateAndGetUserByUserId(message.getAuthor().getId());

        unlinkMessage(author, channel, message);

        if (message.getAttachmentIds() != null && !message.getAttachmentIds().isEmpty()) {
            for (UUID attachmentId : message.getAttachmentIds()) {
                binaryContentRepository.delete(attachmentId);
            }
        }
        messageRepository.delete(messageId);
        channelRepository.save(channel);
        userRepository.save(author);
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
        ValidationMethods.validateId(userId);
        userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("해당 사용자가 없습니다."));
    }
    public User validateAndGetUserByUserId(UUID userId) {
        ValidationMethods.validateId(userId);
        return userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("해당 사용자가 없습니다."));
    }

    // Channel ID null & channel 객체 존재 확인
    public Channel validateAndGetChannelByChannelId(UUID channelId) {
        ValidationMethods.validateId(channelId);
        return channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("해당 채널이 없습니다."));
    }
    public void validateChannelByChannelId(UUID channelId) {
        ValidationMethods.validateId(channelId);
        channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("해당 채널이 없습니다."));
    }

    // Message ID null & Message 객체 존재 확인
    public Message validateAndGetMessageByMessageId(UUID messageId) {
        ValidationMethods.validateId(messageId);
        return messageRepository.findById(messageId)
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
