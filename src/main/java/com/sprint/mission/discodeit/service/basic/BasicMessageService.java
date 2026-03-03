package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.common.exception.code.ErrorCode;
import com.sprint.mission.discodeit.common.exception.custom.APIException;
import com.sprint.mission.discodeit.dto.MessageServiceDTO.MessageCreateCommand;
import com.sprint.mission.discodeit.dto.MessageServiceDTO.MessageResponse;
import com.sprint.mission.discodeit.dto.MessageServiceDTO.MessageUpdateCommand;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.IdGenerator;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicMessageService extends BasicDomainService<Message> implements MessageService {
    private final MessageRepository messageRepository;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final BinaryContentRepository attachmentRepository;
    private final IdGenerator idGenerator;

    @Override
    public MessageResponse create(MessageCreateCommand command) {
        validateUserAndChannelExist(command);

        List<UUID> attachmentIds = command.attachments().stream()
                .map(BinaryContent::new)
                .map(attachmentRepository::save)
                .map(BinaryContent::getId)
                .toList();
        Message message = new Message(idGenerator.generateId(), command.content(), command.channelId(),
                command.authorId(), attachmentIds);
        messageRepository.save(message);
        return message.toResponse();
    }

    @Override
    public List<MessageResponse> findAllByChannelId(UUID channelId) {
        return messageRepository.filter(message -> message.isInChannel(channelId))
                .map(Message::toResponse)
                .toList();
    }

    @Override
    public MessageResponse update(MessageUpdateCommand command) {
        Message message = findById(command.id());
        message.update(command.content(), List.of());
        messageRepository.save(message);
        return message.toResponse();
    }

    @Override
    public void delete(UUID messageId) {
        findById(messageId).toResponse()
                .attachmentIds()
                .forEach(attachmentRepository::deleteById);
        messageRepository.deleteById(messageId);
    }

    @Override
    protected Message findById(UUID id) {
        return findEntityById(id, messageRepository,
                () -> new APIException(ErrorCode.MESSAGEID_NOT_FOUND, id));
    }

    private void validateUserAndChannelExist(MessageCreateCommand command) {
        if (!channelRepository.existsById(command.channelId())) {
            throw new APIException(ErrorCode.CHANNELID_NOT_FOUND, command.channelId());
        }
        if (!userRepository.existsById(command.authorId())) {
            throw new APIException(ErrorCode.USERID_NOT_FOUND, command.authorId());
        }
    }
}
