package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.MessageServiceDTO.MessageContentUpdate;
import com.sprint.mission.discodeit.dto.MessageServiceDTO.MessageCreation;
import com.sprint.mission.discodeit.dto.MessageServiceDTO.MessageResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {
    private final String ID_NOT_FOUND = "Message with id, %s, not found";
    private final MessageRepository messageRepository;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final BinaryContentRepository attachmentRepository;

    @Override
    public MessageResponse create(MessageCreation model) {
        // todo: extract validate
        if (!channelRepository.existsById(model.channelId())) {
            throw new NoSuchElementException("Channel not found with id " + model.channelId());
        }
        if (!userRepository.existsById(model.authorId())) {
            throw new NoSuchElementException("Author not found with id " + model.authorId());
        }
        List<UUID> attachmentIds = model.attachments()
                .stream()
                .map(attachment
                        -> attachmentRepository.save(new BinaryContent(attachment)))
                .map(BinaryContent::getId)
                .toList();
        Message message = new Message(model.content(), model.channelId(), model.authorId(), attachmentIds);
        messageRepository.save(message);
        return message.toResponse();
    }

    @Override
    public List<MessageResponse> findAllByChannelId(UUID channelId) {
        return messageRepository.findAllByChannelId(channelId)
                .stream()
                .map(Message::toResponse)
                .toList();
    }

    @Override
    public MessageResponse update(MessageContentUpdate model) {
        // todo: refactoring
        Message message = messageRepository.findById(model.messageId())
                .orElseThrow(() -> new NoSuchElementException(
                        ID_NOT_FOUND.formatted(model.messageId())));
        message.update(model.newContent(), model.attachmentIds());
        messageRepository.save(message);
        return message.toResponse();
    }

    @Override
    public void delete(UUID messageId) {
        if (!messageRepository.existsById(messageId)) {
            throw new NoSuchElementException(ID_NOT_FOUND.formatted(messageId));
        }
        messageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException(
                        ID_NOT_FOUND.formatted(messageId)))
                .toResponse()
                .attachmentIds()
                .forEach(attachmentRepository::deleteById);
        messageRepository.deleteById(messageId);
    }
}
