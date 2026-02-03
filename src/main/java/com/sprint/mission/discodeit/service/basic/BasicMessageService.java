package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.dto.MessageDto;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public MessageDto.response createMessage(MessageDto.createRequest messageReq,
                                             List<BinaryContentDto.createRequest> contentReqs) {
        Channel channel = getChannelOrThrow(messageReq.channelId());
        User author = getUserOrThrow(messageReq.authorId());

        // 참여여부 확인
        if (channel.getParticipants().stream().noneMatch(u -> Objects.equals(u, messageReq.authorId()))) {
            throw new IllegalStateException("채널에 소속되지 않은 유저입니다.");
        }

        Message msg = new Message(messageReq.channelId(), messageReq.authorId(), messageReq.message());
        contentReqs.forEach(req -> {
            BinaryContent content = new BinaryContent(req.contentType(), req.filename(), req.contentBytes());
            binaryContentRepository.save(content);
            binaryContentRepository.findById(content.getId()).ifPresent(c -> {
                msg.addAttachmentId(c.getId());
            });
        });
        messageRepository.save(msg);

        channel.addMessage(msg.getId());
        channel.updateUpdatedAt();
        channelRepository.save(channel);

        author.addMessageHistory(msg.getId());
        author.updateUpdatedAt();
        userRepository.save(author);

        return toResponse(msg);
    }

    @Override
    public MessageDto.response findMessage(UUID uuid) {
        return messageRepository.findById(uuid)
                .map(this::toResponse)
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 메시지입니다"));
    }

    @Override
    public List<MessageDto.response> findAllByChannelId(UUID channelId) {
        return messageRepository.findAllByChannelId(channelId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public MessageDto.response updateMessage(UUID uuid, MessageDto.updateRequest messageReq) {
        Message msg = messageRepository.findById(uuid)
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 메시지입니다"));

        Optional.ofNullable(messageReq.message()).ifPresent(msg::updateMessage);
        msg.updateUpdatedAt();
        messageRepository.save(msg);

        return toResponse(msg);
    }

    @Override
    public void deleteMessage(UUID uuid) {
        Message msg = messageRepository.findById(uuid)
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 메시지입니다"));
        Channel channel = getChannelOrThrow(msg.getChannelId());
        User author = getUserOrThrow(msg.getAuthorId());

        author.removeMessageHistory(msg.getId());
        author.updateUpdatedAt();
        userRepository.save(author);

        channel.removeMessage(msg.getId());
        channel.updateUpdatedAt();
        channelRepository.save(channel);

        List.copyOf(msg.getAttachmentIds())
                .forEach(binaryContentRepository::deleteById);

        messageRepository.deleteById(uuid);
    }

    private Channel getChannelOrThrow(UUID channelId) {
        return channelRepository.findById(channelId)
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 채널입니다"));
    }

    private User getUserOrThrow(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 유저입니다"));
    }

    private MessageDto.response toResponse(Message msg) {
        return new MessageDto.response(msg.getId(), msg.getCreatedAt(), msg.getUpdatedAt(),
                msg.getChannelId(), msg.getAuthorId(),
                msg.getMessage(), msg.getAttachmentIds());
    }
}
