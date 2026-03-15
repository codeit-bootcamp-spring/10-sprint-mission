package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class BasicMessageService implements MessageService {

  private final MessageRepository messageRepository;
  private final ChannelRepository channelRepository;
  private final UserRepository userRepository;
  private final BinaryContentService binaryContentService;
  private final BinaryContentRepository binaryContentRepository;

  @Override
  @Transactional
  public Message create(MessageCreateRequest request,
                        List<BinaryContentCreateRequest> binaryContentCreateRequests) {

    // Getter 방식으로 변경
    UUID authorId = request.getAuthorId();
    UUID channelId = request.getChannelId();

    User author = userRepository.findById(authorId)
            .orElseThrow(() -> new NoSuchElementException("User not found: " + authorId));
    Channel channel = channelRepository.findById(channelId)
            .orElseThrow(() -> new NoSuchElementException("Channel not found: " + channelId));

    List<BinaryContent> attachments = binaryContentCreateRequests.stream()
            .map(attachmentRequest -> binaryContentService.create(attachmentRequest))
            .toList();

    String content = request.getContent();
    Message message = new Message(content, channel, author, attachments);
    return messageRepository.save(message);
  }

  public Slice<Message> findAllByChannelId(UUID channelId, Pageable pageable) {
    return messageRepository.findAllByChannelId(channelId, pageable);
  }

  @Override
  public Message find(UUID messageId) {
    return messageRepository.findById(messageId)
            .orElseThrow(
                    () -> new NoSuchElementException("Message with id " + messageId + " not found"));
  }

  @Override
  public List<Message> findAllByChannelId(UUID channelId) {
    return messageRepository.findAllByChannelId(channelId);
  }

  @Override
  public Message update(UUID messageId, MessageUpdateRequest request) {
    String newContent = request.getNewContent();
    Message message = messageRepository.findById(messageId)
            .orElseThrow(
                    () -> new NoSuchElementException("Message with id " + messageId + " not found"));
    message.update(newContent);
    return messageRepository.save(message);
  }

  @Override
  public void delete(UUID messageId) {
    Message message = messageRepository.findById(messageId)
            .orElseThrow(
                    () -> new NoSuchElementException("Message with id " + messageId + " not found"));

    message.getAttachmentIds()
            .forEach(binaryContentRepository::deleteById);

    messageRepository.deleteById(messageId);
  }
}
