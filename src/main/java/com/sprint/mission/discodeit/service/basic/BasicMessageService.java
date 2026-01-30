package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.message.MessageCreateRequestDto;
import com.sprint.mission.discodeit.dto.message.MessageResponseDto;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequestDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.mapper.message.MessageResponseMapper;
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

@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {
    private final MessageRepository messageRepository;
    //
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;
    //
    private final MessageResponseMapper messageResponseMapper;

    @Override
    public MessageResponseDto create(MessageCreateRequestDto messageCreateRequestDto) {
        if (!channelRepository.existsById(messageCreateRequestDto.channelId())) {
            throw new NoSuchElementException("Channel not found with id " + messageCreateRequestDto.channelId());
        }
        if (!userRepository.existsById(messageCreateRequestDto.authorId())) {
            throw new NoSuchElementException("Author not found with id " + messageCreateRequestDto.authorId());
        }

        List<UUID> attachmentListToId;
        if(messageCreateRequestDto.attachment() != null){
            attachmentListToId = messageCreateRequestDto.attachment()
                    .content()
                    .stream()
                    .map(binaryContent ->{
                        BinaryContent b = new BinaryContent(binaryContent);
                        binaryContentRepository.save(b);
                        return b.getId();
                    })
                    .toList();
        }
        else{
            attachmentListToId = null;
        }


        Message message = new Message(
                messageCreateRequestDto.content(),
                messageCreateRequestDto.channelId(),
                messageCreateRequestDto.authorId(),
                attachmentListToId
        );
        messageRepository.save(message);

        return messageResponseMapper.toDto(message);
    }

    @Override
    public MessageResponseDto find(UUID messageId) {
        Message targetMessage = messageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException("Message with id " + messageId + " not found"));

        return messageResponseMapper.toDto(targetMessage);
    }

    @Override
    public List<MessageResponseDto> findallByChannelId(UUID channelId) {
        List<Message> messages = messageRepository.findAll().stream()
                .filter(message -> message.getChannelId().equals(channelId))
                .toList();

        //시간 순으로 정렬
        messages.sort((m1, m2) -> m2.getCreatedAt().compareTo(m1.getCreatedAt()));

        return messages.stream()
                .map(messageResponseMapper::toDto)
                .toList();
    }

    @Override
    public MessageResponseDto update(MessageUpdateRequestDto messageUpdateRequestDto) {
        Message message = messageRepository.findById(messageUpdateRequestDto.messageId())
                .orElseThrow(() -> new NoSuchElementException(
                            "Message with id " + messageUpdateRequestDto.messageId() + " not found"
                    ));


            List<UUID> newAttachmentIds = Optional.ofNullable(messageUpdateRequestDto.newAttachment())
                    .map(attachmentDto -> attachmentDto.content())
                    .map(contents -> contents.stream()
                            .map(bytes -> {
                                BinaryContent b = new BinaryContent(bytes);
                                binaryContentRepository.save(b);
                                return b.getId();
                            })
                            .toList()
                    )
                    .orElse(null);

            message.update(messageUpdateRequestDto.newContent(), newAttachmentIds);
            return messageResponseMapper.toDto(message);
        }

    @Override
    public void delete(UUID messageId) {
        if (!messageRepository.existsById(messageId)) {
            throw new NoSuchElementException("Message with id " + messageId + " not found");
        }

        //내부 파일들 바이너리레포에서 삭제
        messageRepository.findById(messageId).orElseThrow()
                .getAttachments().forEach(binaryContentRepository::deleteById);

        // 메시지레포에서 삭제
        messageRepository.deleteById(messageId);
    }
}
