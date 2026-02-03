package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.dto.MessageDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {

    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final ReadStatusRepository readStatusRepository;
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public MessageDto.Response create(MessageDto.Create request) {

        userRepository.findById(request.authorId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        channelRepository.findById(request.channelId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채널입니다."));

        readStatusRepository.findByUserIdAndChannelId(request.authorId(), request.channelId())
                .orElseThrow(() -> new IllegalArgumentException("채널에 참가하지 않은 유저는 글을 작성할 수 없습니다."));

        //메시지 객체 내부의 첨부파일Id 리스트 저장용
        List<UUID> attachmentIds = new ArrayList<>();
        //요청에 첨부파일이 있다면 for-loop를 통해 객체 생성 후 저장
        if (!request.attachments().isEmpty()) {
            for (BinaryContentDto.Create attachmentDto : request.attachments()) {
                BinaryContent attachment = new BinaryContent(
                        attachmentDto.fileName(),
                        attachmentDto.bytes()
                );
                binaryContentRepository.save(attachment);
                attachmentIds.add(attachment.getId());
            }
        }
        Message message = new Message(
                request.content(),
                request.authorId(),
                request.channelId(),
                attachmentIds
        );
        messageRepository.save(message);

        return MessageDto.Response.of(message);
    }

    @Override
    public MessageDto.Response findById(UUID messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 메시지입니다."));
        return MessageDto.Response.of(message);
    }

    @Override
    public List<MessageDto.Response> findAllByChannelId(UUID channelId) {
        return messageRepository.findAll().stream()
                .filter(message -> message.getChannelId().equals(channelId))
                .map(MessageDto.Response::of)
                .toList();
    }

    @Override
    public MessageDto.Response update(UUID authorId, MessageDto.Update request) {
        Message message = messageRepository.findById(request.id())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 메시지입니다."));

        if (!authorId.equals(message.getAuthorId())) {
            throw new IllegalArgumentException("작성자만이 글을 수정할 수 있습니다.");
        }

        message.update(request.content());
        messageRepository.save(message);
        return MessageDto.Response.of(message);
    }

    @Override
    public void delete(UUID authorId, UUID messageId) {
        Message message = messageRepository.findById(messageId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 메시지입니다."));

        if (!authorId.equals(message.getAuthorId())) {
            throw new IllegalArgumentException("작성자만이 글을 수정할 수 있습니다.");
        }

        for (UUID attachmentId : message.getAttachmentIds()) {
            binaryContentRepository.findById(attachmentId)
                    .ifPresent(binaryContentRepository::delete);
        }
        messageRepository.delete(message);
    }
}
