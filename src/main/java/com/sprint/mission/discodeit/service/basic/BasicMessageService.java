package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.dto.MessageDto;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.exception.BusinessLogicException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final BinaryContentRepository binaryContentRepository;
    @Value("${discodeit.upload.attachment}")
    private String ATTACHMENT_DIR;

    @Override
    public MessageDto.messageResponse createMessage(MessageDto.messageCreateRequest messageReq,
                                                    List<BinaryContentDto.binaryContentCreateRequest> contentReqs) {
        Channel channel = getChannelOrThrow(messageReq.channelId());
        User author = getUserOrThrow(messageReq.authorId());

        // 참여여부 확인
        if (channel.getChannelType() == ChannelType.PRIVATE
                && channel.getParticipants().stream().noneMatch(u -> Objects.equals(u, messageReq.authorId()))) {
            throw new BusinessLogicException(ErrorCode.MESSAGE_WRITE_FORBIDDEN);
        }

        Message msg = new Message(messageReq.channelId(), messageReq.authorId(), messageReq.message());
        Optional.ofNullable(contentReqs).ifPresent(reqs -> {
            Path attachmentDir = Paths.get(ATTACHMENT_DIR);
            reqs.forEach(req -> {
                BinaryContent content = new BinaryContent(req.contentType(), req.filename(), ATTACHMENT_DIR);
                // 첨부파일 저장
                String fileName = content.getId() + "." + StringUtils.getFilenameExtension(req.filename());
                try {
                    Files.createDirectories(attachmentDir);
                    Files.write(attachmentDir.resolve(fileName), req.bytes());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                binaryContentRepository.save(content);
                binaryContentRepository.findById(content.getId()).ifPresent(c -> {
                    msg.addAttachmentId(c.getId());
                });
            });
        });
        messageRepository.save(msg);

        channel.addMessage(msg.getId());
        channel.updateUpdatedAt();
        channelRepository.save(channel);

        author.addMessageHistory(msg.getId());
        author.updateUpdatedAt();
        userRepository.save(author);

        return toResponse(msg);
    }

    @Override
    public MessageDto.messageResponse findMessage(UUID uuid) {
        return messageRepository.findById(uuid)
                .map(this::toResponse)
                .orElseThrow(() -> new BusinessLogicException(ErrorCode.MESSAGE_NOT_FOUND));
    }

    @Override
    public List<MessageDto.messageResponse> findAllByChannelId(UUID channelId) {
        return messageRepository.findAllByChannelId(channelId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public MessageDto.messageResponse updateMessage(UUID uuid, MessageDto.messageUpdateRequest messageReq) {
        Message msg = messageRepository.findById(uuid)
                .orElseThrow(() -> new BusinessLogicException(ErrorCode.MESSAGE_NOT_FOUND));

        Optional.ofNullable(messageReq.message()).ifPresent(msg::updateMessage);
        msg.updateUpdatedAt();
        messageRepository.save(msg);

        return toResponse(msg);
    }

    @Override
    public void deleteMessage(UUID uuid) {
        Message msg = messageRepository.findById(uuid)
                .orElseThrow(() -> new BusinessLogicException(ErrorCode.MESSAGE_NOT_FOUND));
        Channel channel = getChannelOrThrow(msg.getChannelId());
        User author = getUserOrThrow(msg.getAuthorId());

        author.removeMessageHistory(msg.getId());
        author.updateUpdatedAt();
        userRepository.save(author);

        channel.removeMessage(msg.getId());
        channel.updateUpdatedAt();
        channelRepository.save(channel);

        // 메시지에 있는 첨부파일 삭제
        deleteAttachmentIfExists(List.copyOf(msg.getAttachmentIds()));

        messageRepository.deleteById(uuid);
    }

    private void deleteAttachmentIfExists(List<UUID> attachmentIds) {
        Path dir = Paths.get(ATTACHMENT_DIR);

        for (UUID attachmentId: attachmentIds) {
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, attachmentId + ".*")) {
                for (Path p : stream) {
                    Files.deleteIfExists(p);
                    break;
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            binaryContentRepository.deleteById(attachmentId);
        }
    }

    private Channel getChannelOrThrow(UUID channelId) {
        return channelRepository.findById(channelId)
                .orElseThrow(() -> new BusinessLogicException(ErrorCode.CHANNEL_NOT_FOUND));
    }

    private User getUserOrThrow(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessLogicException(ErrorCode.USER_NOT_FOUND));
    }

    private MessageDto.messageResponse toResponse(Message msg) {
        return new MessageDto.messageResponse(msg.getId(), msg.getCreatedAt(), msg.getUpdatedAt(),
                msg.getChannelId(), msg.getAuthorId(),
                msg.getMessage(), msg.getAttachmentIds());
    }
}
