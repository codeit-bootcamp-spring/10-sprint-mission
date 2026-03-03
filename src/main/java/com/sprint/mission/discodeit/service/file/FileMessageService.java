package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.dto.UserResponse;
import com.sprint.mission.discodeit.dto.message.CreateMessageRequest;
import com.sprint.mission.discodeit.dto.message.MessageResponse;
import com.sprint.mission.discodeit.dto.message.UpdateMessageRequest;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.status.BinaryContent;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.status.BinaryContentRepository;
import com.sprint.mission.discodeit.service.MessageService;
import java.util.NoSuchElementException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class FileMessageService implements MessageService {

  private final MessageRepository messageRepository;
  private final ChannelRepository channelRepository;
  private final UserRepository userRepository;
  private final BinaryContentRepository binaryContentRepository;

  public FileMessageService(MessageRepository messageRepository,
      ChannelRepository channelRepository,
      UserRepository userRepository,
      BinaryContentRepository binaryContentRepository) {
    this.messageRepository = messageRepository;
    this.channelRepository = channelRepository;
    this.userRepository = userRepository;
    this.binaryContentRepository = binaryContentRepository;
  }

  @Override
  public Message create(MessageCreateRequest request,
      List<BinaryContentCreateRequest> binaryContentCreateRequests) {
    UUID channelId = request.channelId();
    UUID authorId = request.authorId();

    channelRepository.findById(channelId)
        .orElseThrow(() -> new NoSuchElementException("Channel not found: " + channelId));
    userRepository.findById(authorId)
        .orElseThrow(() -> new NoSuchElementException("User not found: " + authorId));

    List<UUID> attachmentIds = binaryContentCreateRequests.stream()
        .map(req -> {
          BinaryContent bc = new BinaryContent(req.fileName(), (long) req.bytes().length,
              req.contentType(), req.bytes());
          return binaryContentRepository.save(bc).getId();
        })
        .toList();

    Message message = new Message(request.content(), channelId, authorId, attachmentIds);
    return messageRepository.save(message);
  }

  @Override
  public Message find(UUID messageId) {
    return messageRepository.findById(messageId)
        .orElseThrow(() -> new NoSuchElementException("Message not found: " + messageId));
  }

  @Override
  public List<MessageResponse> findAllByChannelId(UUID channelId) {
    return messageRepository.findAllByChannelId(channelId).stream()
        .map(message -> {
          UserResponse author = userRepository.findById(message.getAuthorId())
              .map(user -> UserResponse.from(user, null))
              .orElse(null);
          return MessageResponse.from(message, author, message.getAttachmentIds());
        })
        .toList();
  }

  @Override
  public Message update(UUID messageId, MessageUpdateRequest request) {
    Message message = messageRepository.findById(messageId)
        .orElseThrow(() -> new NoSuchElementException("Message not found: " + messageId));
    message.update(request.newContent());
    return messageRepository.save(message);
  }

  @Override
  public void delete(UUID messageId) {
    Message message = messageRepository.findById(messageId)
        .orElseThrow(() -> new NoSuchElementException("Message not found: " + messageId));
    message.getAttachmentIds().forEach(binaryContentRepository::deleteById);
    messageRepository.deleteById(messageId);
  }
}
