package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.message.MessageRequestDto;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@AllArgsConstructor
public class BasicMessageService implements MessageService {
    private final MessageRepository messageRepository;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;



    @Override
    public Message create(MessageRequestDto messageCreateDto) {//DTO를 활용해 파라미터를 그룹화 합니다.
        if (!channelRepository.existsById(messageCreateDto.channelId())) {
            throw new NoSuchElementException("Channel not found with id " + messageCreateDto.channelId());
        }
        if (!userRepository.existsById(messageCreateDto.authorId())) {
            throw new NoSuchElementException("Author not found with id " + messageCreateDto.authorId());
        }

        Message message = new Message(
                messageCreateDto.content(),
                messageCreateDto.channelId(),
                messageCreateDto.authorId(),
                messageCreateDto.attachmentIds()
        );

        return messageRepository.save(message);
    }

    @Override
    public List<Message> findallByChannelId(UUID channelId) {

        return messageRepository.findByChannelId(channelId);
    }

    @Override
    public Message update(UUID messageId, MessageRequestDto messageUpdateDto) {//DTO를 활용해 파라미터를 그룹화 합니다.
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException("Message with id " + messageId + " not found"));
        message.update(messageUpdateDto.content());
        return messageRepository.save(message);
    }

    @Override
    public void delete(UUID messageId) {
        Message message = messageRepository.findById(messageId).orElseThrow(() -> new NoSuchElementException("Message with id " + messageId + " not found"));
        for (UUID attachmentId : message.getAttachmentIds()) {
            binaryContentRepository.deleteById(attachmentId);
        }
        messageRepository.deleteById(messageId);
    }
}
