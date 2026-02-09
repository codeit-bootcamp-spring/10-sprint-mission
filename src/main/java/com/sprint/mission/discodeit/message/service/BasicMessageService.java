package com.sprint.mission.discodeit.message.service;

import com.sprint.mission.discodeit.message.dto.MessageCreateInfo;
import com.sprint.mission.discodeit.message.dto.MessageInfo;
import com.sprint.mission.discodeit.message.dto.MessageUpdateInfo;
import com.sprint.mission.discodeit.binarycontent.BinaryContent;
import com.sprint.mission.discodeit.channel.Channel;
import com.sprint.mission.discodeit.message.Message;
import com.sprint.mission.discodeit.user.User;
import com.sprint.mission.discodeit.message.MessageMapper;
import com.sprint.mission.discodeit.binarycontent.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.channel.repository.ChannelRepository;
import com.sprint.mission.discodeit.message.repository.MessageRepository;
import com.sprint.mission.discodeit.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class BasicMessageService implements MessageService {
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final BinaryContentRepository contentRepository;

    @Override
    public MessageInfo createMessage(MessageCreateInfo createInfo) {
        List<UUID> attachmentIds = createInfo.attachments()
                .stream()
                .map(BinaryContent::new)
                .peek(contentRepository::save)
                .map(BinaryContent::getId)
                .toList();

        Message message = new Message(createInfo.content(), createInfo.senderID(),
                createInfo.channelID(), attachmentIds);

        User sender = userRepository.findById(message.getSenderId())
                        .orElseThrow(() -> new NoSuchElementException("발신자를 찾을 수 없습니다."));
        Channel findChannel = channelRepository.findById(message.getChannelId())
                        .orElseThrow(() -> new NoSuchElementException("채널을 찾을 수 없습니다."));

        sender.addMessageId(message.getId());
        findChannel.addMessageId(message.getId());

        userRepository.save(sender);
        channelRepository.save(findChannel);
        messageRepository.save(message);
        return MessageMapper.toMessageInfo(message);
    }

    @Override
    public MessageInfo findMessage(UUID messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException("해당 메세지가 존재하지 않습니다."));

        return MessageMapper.toMessageInfo(message);
    }

    @Override
    public List<MessageInfo> findAll() {
        return toMessageInfoList(messageRepository.findAll());
    }

    @Override
    public List<MessageInfo> findAllByUserId(UUID userId) {
        return toMessageInfoList(messageRepository.findAllByUserId(userId));
    }

    @Override
    public List<MessageInfo> findAllByChannelId(UUID channelId) {
        return toMessageInfoList(messageRepository.findAllByChannelId(channelId));
    }

    @Override
    public MessageInfo updateMessage(MessageUpdateInfo messageInfo) {
        Message findMessage = messageRepository.findById(messageInfo.messageId())
                .orElseThrow(() -> new NoSuchElementException("해당 메세지가 존재하지 않습니다."));

        Optional.ofNullable(messageInfo.content())
                .ifPresent(findMessage::updateContent);

        messageRepository.save(findMessage);
        return MessageMapper.toMessageInfo(findMessage);
    }

    @Override
    public void deleteMessage(UUID messageId) {
        Message findMessage = messageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException("해당 메세지가 존재하지 않습니다."));
        User findUser = userRepository.findById(findMessage.getSenderId())
                .orElseThrow(() -> new NoSuchElementException("메세지의 발신자가 존재하지 않습니다."));
        Channel findChannel = channelRepository.findById(findMessage.getChannelId())
                .orElseThrow(() -> new NoSuchElementException("메세지의 채널이 존재하지 않습니다."));

        findMessage.getAttachmentIds()
                .forEach(contentRepository::deleteById);

        findUser.removeMessageId(messageId);
        findChannel.removeMessageId(messageId);

        userRepository.save(findUser);
        channelRepository.save(findChannel);
        messageRepository.deleteById(messageId);
    }

    private List<MessageInfo> toMessageInfoList(List<Message> messages) {
        return messages.stream()
                .map(MessageMapper::toMessageInfo)
                .toList();
    }
}
