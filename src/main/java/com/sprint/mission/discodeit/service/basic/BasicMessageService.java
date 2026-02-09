package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.dto.MessageDto;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {
    private final MessageRepository messageRepository;
    //
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final ReadStatusRepository readStatusRepository;
    //
    private final MessageMapper messageMapper;


    @Override
    public MessageDto.Response create(MessageDto.CreateRequest request, List<MultipartFile> attachments) {
        Channel channel = channelRepository.findById(request.channelId())
                .orElseThrow(() -> new NoSuchElementException("해당 채널을 찾을 수 없습니다: " + request.channelId()));

        if (!userRepository.existsById(request.authorId())) {
            throw new NoSuchElementException("해당 유저를 찾을 수 없습니다: " + request.authorId());
        }

        if (channel.getType() == ChannelType.PRIVATE) {
            if (!readStatusRepository.existsByUserIdAndChannelId(request.authorId(), channel.getId())){
                throw new IllegalArgumentException("비공개 채널에는 채널 멤버만 메시지를 전송할 수 있습니다.");
            }
        }

        List<UUID> attachmentIds = Optional.ofNullable(attachments)
                .orElse(List.of())
                .stream()
                .map(this::saveAttachments)
                .toList();

        Message message = new Message(request.content(), request.channelId(), request.authorId(), attachmentIds);
        Message savedMessage = messageRepository.save(message);

        channel.updateLastMessageAt(savedMessage.getCreatedAt());
        channelRepository.save(channel);

        return messageMapper.toResponse(savedMessage);
    }

    @Override
    public MessageDto.Response find(UUID messageId) {
        return messageRepository.findById(messageId)
                .map(messageMapper::toResponse)
                .orElseThrow(() -> new NoSuchElementException("해당 메시지를 찾을 수 없습니다: " + messageId));
    }

    @Override
    public List<MessageDto.Response> findAllByChannelId(UUID channelId) {
        if(!channelRepository.existsById(channelId)) {
            throw new NoSuchElementException("해당 채널을 찾을 수 없습니다: "  + channelId);
        }
        return messageRepository.findAllByChannelId(channelId).stream()
                .map(messageMapper::toResponse)
                .toList();
    }

    @Override
    public MessageDto.Response update(UUID messageId, MessageDto.UpdateRequest request) {
        String newContent = request.content();

        // 메시지를 수정할 때 빈 메시지로 바꿀 때 삭제되는 로직을 구현하려고 하였음
        // 그러나 삭제 로직이 업데이트에 들어있어서 책임 분리가 필요함
        // 실제 서비스 시에는 프론트엔드에서 빈 메시지로 수정 시에 delete를 호출하도록 하는게 맞는 설계일 듯 함
//        if (newContent == null) {
//            delete(messageId); // 메시지를 수정할 때 빈 메시지로 하면 삭제됨
//            return null;
//        }

        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException("해당 메시지를 찾을 수 없습니다: " + messageId));
        message.update(newContent);

        return messageMapper.toResponse(messageRepository.save(message));
    }

    @Override
    public void delete(UUID messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException("해당 메시지를 찾을 수 없습니다: " + messageId));

        message.getAttachmentIds()
                .forEach(binaryContentRepository::deleteById);

        messageRepository.deleteById(messageId);
    }

    private UUID saveAttachments(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        try {
            BinaryContent content = new BinaryContent(
                    file.getOriginalFilename(),
                    file.getContentType(),
                    file.getBytes()
            );

            return binaryContentRepository.save(content).getId();
        } catch (IOException e) {
            throw new UncheckedIOException("첨부파일 저장 중 오류가 발생했습니다: " + file.getOriginalFilename(), e);
        }
    }
}
