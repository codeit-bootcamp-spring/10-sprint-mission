package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.exception.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.MessageNotFoundException;
import com.sprint.mission.discodeit.exception.UserNotFoundException;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
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
    //
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public Message create(MessageCreateRequest messageCreateRequest) {
        UUID channelId = messageCreateRequest.channelId();
        UUID authorId = messageCreateRequest.authorId();

        if (!channelRepository.existsById(channelId)) {
            throw new ChannelNotFoundException(channelId + " 에 해당하는 채널이 없습니다.");
        }
        if (!userRepository.existsById(authorId)) {
            throw new UserNotFoundException(authorId + "에 해당하는 사용자가 없습니다.");
        }

        List<UUID> attachmentIds =
                Optional.ofNullable(messageCreateRequest.binaryContentCreateRequests())
                        .orElse(List.of())
                        .stream()
                        .map(attachmentRequest -> {
                            BinaryContent binaryContent = new BinaryContent(
                                    attachmentRequest.fileName(),
                                    (long) attachmentRequest.bytes().length,
                                    attachmentRequest.contentType(),
                                    attachmentRequest.bytes()
                            );
                            return binaryContentRepository.save(binaryContent).getId();
                        })
                        .toList();

        String content = messageCreateRequest.content();
        Message message = new Message(
                content,
                channelId,
                authorId,
                attachmentIds
        );
        return messageRepository.save(message);
    }

    @Override
    public Message find(UUID messageId) {
        return messageRepository.findById(messageId)
                .orElseThrow(() -> new MessageNotFoundException(messageId + " 에 해당하는 메시지가 없습니다."));
    }

    @Override
    public List<Message> findAllByChannelId(UUID channelId) {
        return messageRepository.findAllByChannelId(channelId).stream()
                .toList();
    }

    @Override
    public Message update(UUID messageId, MessageUpdateRequest request) {
        String newContent = request.newContent();
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new MessageNotFoundException(messageId + " 에 해당하는 메시지가 없습니다."));
        message.update(newContent);
        return messageRepository.save(message);
    }

    @Override
    public void delete(UUID messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new MessageNotFoundException(messageId + " 에 해당하는 메시지가 없습니다."));

        message.getAttachmentIds()
                .forEach(binaryContentRepository::deleteById);

        messageRepository.deleteById(messageId);
    }
}
