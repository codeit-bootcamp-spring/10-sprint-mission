package com.sprint.mission.discodeit.message.application.handler;

import com.sprint.mission.discodeit.binarycontent.domain.event.BinaryContentUpdatedEvent;
import com.sprint.mission.discodeit.message.domain.entity.Message;
import com.sprint.mission.discodeit.message.domain.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.message.domain.event.MessageDeletedEvent;
import com.sprint.mission.discodeit.message.domain.event.MessageUpdatedEvent;
import com.sprint.mission.discodeit.message.domain.provider.MessageNotifier;
import com.sprint.mission.discodeit.message.domain.repository.MessageRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class MessageNotifierEventHandler {

  private final MessageRepository repository;
  private final MessageNotifier notifier;

  @Async("stompWorker")
  @TransactionalEventListener
  public void handleCreated(MessageCreatedEvent event) {
    notifier.notifyCreated(event.getChannelId(), event);
  }

  @Async("stompWorker")
  @TransactionalEventListener
  public void handleUpdated(MessageUpdatedEvent event) {
    notifier.notifyUpdated(event.getChannelId(), event);
  }

  @Async("stompWorker")
  @TransactionalEventListener
  public void handleDeleted(MessageDeletedEvent event) {
    notifier.notifyDeleted(event.getChannelId(), event);
  }

  @Async("stompWorker")
  @TransactionalEventListener
  @Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
  public void handleAttachmentUpdatedEvent(BinaryContentUpdatedEvent event) {
    List<Message> messages = repository.findMessagesByAttachmentIds(event.getIds());
    if (messages.isEmpty()) {
      return;
    }
    Message message = messages.get(0);
    UUID channelId = message.getChannel().getId();
    notifier.notifyUpdated(channelId,
        MessageUpdatedEvent.builder()
            .id(message.getId())
            .attachmentIds(event.getIds())
            .attachmentStatus(event.getStatus().toString())
            .build());
  }
}
