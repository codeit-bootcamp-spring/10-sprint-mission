package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.message.MessageCreateDto;
import com.sprint.mission.discodeit.dto.message.MessageResponseDto;
import com.sprint.mission.discodeit.dto.message.MessageUpdateDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {
    private final MessageRepository messageRepository;
    //
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    //
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public MessageResponseDto create(MessageCreateDto dto) {
        if (!channelRepository.existsById(dto.channelId())) {
            throw new NoSuchElementException("Channel not found with id " + dto.channelId());
        }
        if (!userRepository.existsById(dto.authorId())) {
            throw new NoSuchElementException("Author not found with id " + dto.authorId());
        }
        List<UUID> attachmentIds = new ArrayList<>();
        if(dto.attachments()!=null && !dto.attachments().isEmpty()){
            dto.attachments()
                    .forEach((attachment) -> {
                        BinaryContent content = binaryContentRepository.save(new BinaryContent(attachment.fileName(), attachment.bytes()));
                        attachmentIds.add(content.getId());
                    });
        }
        Message message = new Message(dto.content(),dto.channelId(),dto.authorId(),attachmentIds);
        return messageToDto(messageRepository.save(message));
    }

    @Override
    public MessageResponseDto find(UUID messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException("Message with id " + messageId + " not found"));
        return messageToDto(message);
    }

    @Override
    public List<MessageResponseDto> findAllByChannelId(UUID channelId) {
        List<MessageResponseDto> response = new ArrayList<>();
        messageRepository.findAllByChannelId(channelId)
                .forEach(message -> response.add(messageToDto(message)));
        return response;
    }

    @Override
    public MessageResponseDto update(UUID id,MessageUpdateDto dto) {
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Message with id " + id + " not found"));
        message.update(dto.content(),null);//첨부파일 변경을 하려면 별도로 메서드 필요
        return messageToDto(messageRepository.save(message));
    }

    @Override
    public void delete(UUID messageId) {
        Message message = messageRepository.findById(messageId)
                        .orElseThrow(()-> new NoSuchElementException("Message with id " + messageId + " not found"));
        if(message.getAttachmentIds()!=null && !message.getAttachmentIds().isEmpty()){
            message.getAttachmentIds().forEach(binaryContentRepository::delete);//첨부파일 있는경우만 지우기
        }
        messageRepository.deleteById(messageId);

    }
    private MessageResponseDto messageToDto(Message message) {
        return new MessageResponseDto(message.getId(),
                message.getContent(),
                message.getChannelId(),
                message.getAuthorId(),
                message.getAttachmentIds(),
                message.getCreatedAt(),
                message.getUpdatedAt()
        );
    }
}
