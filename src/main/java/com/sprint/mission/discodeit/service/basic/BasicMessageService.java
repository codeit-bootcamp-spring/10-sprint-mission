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
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
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

    @SneakyThrows
    @Override
    public MessageResponseDto create(UUID channelId, MessageCreateRequestDto messageCreateRequestDto, List<MultipartFile> files) {
        if (!channelRepository.existsById(channelId)) {
            throw new NoSuchElementException("Channel not found with id " + channelId);
        }
        if (!userRepository.existsById(messageCreateRequestDto.authorId())) {
            throw new NoSuchElementException("Author not found with id " + messageCreateRequestDto.authorId());
        }

        List<UUID> attachmentListToId = files.stream()
                .filter(file -> file != null && !file.isEmpty())
                .map(file -> {
                    try {
                        BinaryContent b = new BinaryContent(file.getBytes());
                        binaryContentRepository.save(b);
                        return b.getId();
                    } catch (IOException e) {
                        throw new RuntimeException("Failed to read multipart file", e);
                    }
                })
                .toList();

        Message message = new Message(
                messageCreateRequestDto.content(),
                channelId,
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
    public List<MessageResponseDto> findAllByChannelId(UUID channelId) {
        List<Message> messages = messageRepository.findAll().stream()
                .filter(message -> message.getChannelId().equals(channelId))
                .sorted((m1, m2) -> m2.getCreatedAt().compareTo(m1.getCreatedAt()))
                .toList();

        return messages.stream()
                .map(messageResponseMapper::toDto)
                .toList();
    }

    @Override
    public MessageResponseDto update(UUID id, MessageUpdateRequestDto messageUpdateRequestDto, List<MultipartFile> files) {
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(
                            "Message with id " + id + " not found"
                    ));


            List<UUID> newAttachmentIds = Optional.ofNullable(files)
                    .filter(contents-> !contents.isEmpty())
                    .map(contents -> contents.stream()
                            .filter(file -> file != null && !file.isEmpty())
                            .map(file -> {
                                try {
                                    BinaryContent b = new BinaryContent(file.getBytes());
                                    binaryContentRepository.save(b);
                                    return b.getId();
                                } catch (IOException e) {
                                    throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to read attachment", e);
                                }
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
