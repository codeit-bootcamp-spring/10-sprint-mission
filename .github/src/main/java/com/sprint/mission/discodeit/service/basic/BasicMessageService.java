package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.MessageDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Message;
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
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public MessageDto.Response create(MessageDto.CreateRequest request) {

        List<UUID> attachmentIds = new ArrayList<>();

        if (request.attachments() != null && !request.attachments().isEmpty()) {
            attachmentIds = request.attachments().stream()
                    .map(fileRequest -> {
                        BinaryContent content = new BinaryContent(
                                fileRequest.data(),
                                fileRequest.fileType(),
                                fileRequest.fileName()
                        );
                        return binaryContentRepository.save(content).getId();
                    })
                    .toList();
        }

        Message message = new Message(
                request.content(),
                request.channelId(),
                request.authorId(),
                attachmentIds
        );

        messageRepository.save(message);

        return convertToResponse(message);

    }

    @Override
    public List<MessageDto.Response> findAllByChannelId(UUID channelId) {
        return messageRepository.findAllByChannelId(channelId).stream()
                .map(this::convertToResponse)
                .toList();
    }

    @Override
    public MessageDto.Response update(MessageDto.UpdateRequest request) {
        Message message = messageRepository.findById(request.id())
                .orElseThrow(() -> new NoSuchElementException("Message with id " + request.id() + " not found"));

        List<UUID> newIds = Optional.ofNullable(request.attachments())
                .filter(attachments -> !attachments.isEmpty())
                .map(attachments -> attachments.stream()
                        .map(dto -> {
                            BinaryContent content = new BinaryContent(
                                    dto.data(),
                                    dto.fileType(),
                                    dto.fileName()
                            );
                            return binaryContentRepository.save(content).getId();
                        })
                        .toList()
                )
                .orElse(null);
                    message.update(request.content(), newIds);

        return convertToResponse(messageRepository.save(message));

    }


    @Override
    public void delete(UUID id) {
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Message with id " + id + " not found"));

        // 첨부파일 삭제
        if (message.getAttachmentIds() != null && !message.getAttachmentIds().isEmpty()) {
            binaryContentRepository.deleteAllById(message.getAttachmentIds());
        }

        messageRepository.deleteById(id);

    }

    private MessageDto.Response convertToResponse(Message message) {
        return MessageDto.Response.builder()
                .id(message.getId())
                .channelId(message.getChannelId())
                .authorId(message.getAuthorId())
                .content(message.getContent())
                .attachmentId(message.getAttachmentIds())
                .build();
    }
}
