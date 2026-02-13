package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binaryContent.BinaryContentRequestDto;
import com.sprint.mission.discodeit.dto.message.MessageRequestDto;
import com.sprint.mission.discodeit.dto.message.MessageResponseDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
    public MessageResponseDto create(MessageRequestDto messageCreateDto) {//DTO를 활용해 파라미터를 그룹화 합니다.
        if (!channelRepository.existsById(messageCreateDto.channelId())) {
            throw new NoSuchElementException("Channel not found with id " + messageCreateDto.channelId());
        }
        if (!userRepository.existsById(messageCreateDto.authorId())) {
            throw new NoSuchElementException("Author not found with id " + messageCreateDto.authorId());
        }

        List<UUID> binaryContentIds = new ArrayList<>();

        if(messageCreateDto.binaryContentRequestDto() != null && !messageCreateDto.binaryContentRequestDto().isEmpty()) {
            List<BinaryContentRequestDto> binaryContentRequestDtos = messageCreateDto.binaryContentRequestDto();

            for (BinaryContentRequestDto fileDto : binaryContentRequestDtos) {
                BinaryContent binaryContent = new BinaryContent(
                        null,
                        fileDto.data(),
                        fileDto.contentType(),
                        fileDto.fileName()

                );
                binaryContentRepository.save(binaryContent);
                binaryContentIds.add(binaryContent.getId());

            }

        }
        Message message = new Message(
                messageCreateDto.content(),
                messageCreateDto.channelId(),
                messageCreateDto.authorId(),
                binaryContentIds
        );

        messageRepository.save(message);

        return new  MessageResponseDto(
                message.getCreatedAt(),
                message.getUpdatedAt(),
                message.getContent(),
                message.getChannelId(),
                message.getAuthorId(),
                message.getAttachmentIds()
        );
    }

    @Override
    public List<MessageResponseDto> findallByChannelId(UUID channelId) {
        List<Message>  messages = messageRepository.findByChannelId(channelId);
        List<MessageResponseDto> responseDtos = new ArrayList<>();

        for (Message message : messages) {
            responseDtos.add(new  MessageResponseDto(
                    message.getCreatedAt(),
                    message.getUpdatedAt(),
                    message.getContent(),
                    message.getChannelId(),
                    message.getAuthorId(),
                    message.getAttachmentIds()
            ));

        }

        return responseDtos;
    }

    @Override
    public MessageResponseDto update(UUID messageId, MessageRequestDto messageUpdateDto) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException("Message with id " + messageId + " not found"));

        List<UUID> binaryContentIds = messageUpdateDto.binaryContentRequestDto()
                .stream()
                .map(dto -> {
                    BinaryContent binaryContent = new BinaryContent(
                            dto.userId(),
                            dto.data(),
                            dto.contentType(),
                            dto.fileName()
                    );
                    binaryContentRepository.save(binaryContent);
                    return binaryContent.getId();
                }).toList();

        message.update(messageUpdateDto.content(),binaryContentIds);
        messageRepository.save(message);

        return new MessageResponseDto(
                message.getCreatedAt(),
                message.getUpdatedAt(),
                message.getContent(),
                message.getChannelId(),
                message.getAuthorId(),
                message.getAttachmentIds()
        );
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
