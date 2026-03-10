package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.MessageNotFoundException;
import com.sprint.mission.discodeit.exception.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.PageResponseMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Transactional
public class BasicMessageService implements MessageService {
    private final MessageRepository messageRepository;
    //
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final BinaryContentStorage binaryContentStorage;
    private final PageResponseMapper pageResponseMapper;

    @Override
    public Message create(MessageCreateRequest messageCreateRequest) {
        UUID channelId = messageCreateRequest.channelId();
        UUID authorId = messageCreateRequest.authorId();

        User author = userRepository.findById(authorId).orElseThrow(() -> new UserNotFoundException("Message내" + authorId + "에 해당하는 유저가 없습니다."));
        Channel channel = channelRepository.findById(channelId).orElseThrow(() -> new ChannelNotFoundException("Message내" + channelId + "에 해당하는 채널이 없습니다."));

        List<BinaryContent> attachments =
                Optional.ofNullable(messageCreateRequest.binaryContentCreateRequests())
                        .orElse(List.of())
                        .stream()
                        .map(attachmentRequest -> {
                            BinaryContent binaryContent = new BinaryContent(
                                    attachmentRequest.fileName(),
                                    (long) attachmentRequest.bytes().length,
                                    attachmentRequest.contentType()
                            );
                            BinaryContent savedBinaryContent = binaryContentRepository.save(binaryContent);
                            binaryContentStorage.put(savedBinaryContent.getId(),attachmentRequest.bytes());
                            return  savedBinaryContent;
                        })
                        .toList();


        String content = messageCreateRequest.content();
        Message message = new Message(
                content,
                channel,
                author,
                attachments
        );
        return messageRepository.save(message);
    }

    @Override
    @Transactional(readOnly = true)
    public Message find(UUID messageId) {
        return messageRepository.findById(messageId)
                .orElseThrow(() -> new MessageNotFoundException(messageId + " 에 해당하는 메시지가 없습니다."));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Message> findAllByChannelId(UUID channelId, Pageable pageable) {
        return messageRepository.findAllByChannelId(channelId, pageable);
    }

    @Override
    public Message update(UUID messageId, MessageUpdateRequest request) {
        String newContent = request.newContent();
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new MessageNotFoundException(messageId + " 에 해당하는 메시지가 없습니다."));
        message.update(newContent);
        return message;
    }

    @Override
    public void delete(UUID messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new MessageNotFoundException(messageId + " 에 해당하는 메시지가 없습니다."));

        for (BinaryContent binaryContent : message.getAttachments()) {
            binaryContentRepository.deleteById(binaryContent.getId());
        }
        messageRepository.deleteById(messageId);
    }
}
