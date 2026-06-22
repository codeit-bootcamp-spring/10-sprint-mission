package com.sprint.mission.discodeit.channel.application.handler;

import com.sprint.mission.discodeit.channel.domain.event.ChannelUpdatedEvent;
import com.sprint.mission.discodeit.channel.domain.event.PrivateChannelCreatedEvent;
import com.sprint.mission.discodeit.channel.domain.event.PrivateChannelDeletedEvent;
import com.sprint.mission.discodeit.channel.domain.event.PublicChannelCreatedEvent;
import com.sprint.mission.discodeit.channel.domain.event.PublicChannelDeletedEvent;
import com.sprint.mission.discodeit.channel.domain.provider.ChannelNotifier;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class ChannelNotifierEventHandler {

  private final ChannelNotifier notifier;

  @Async("stompWorker")
  @TransactionalEventListener
  public void handleCreated(PublicChannelCreatedEvent event) {
    notifier.notifyCreated(event);
  }

  @Async("stompWorker")
  @TransactionalEventListener
  public void handleCreated(PrivateChannelCreatedEvent event) {
    for (UUID participantId : event.getParticipantIds()) {
      notifier.notifyCreated(participantId, event);
    }
  }

  @Async("stompWorker")
  @TransactionalEventListener
  public void handleUpdated(ChannelUpdatedEvent event) {
    notifier.notifyUpdated(event);
  }

  @Async("stompWorker")
  @TransactionalEventListener
  public void handleDeleted(PublicChannelDeletedEvent event) {
    notifier.notifyDeleted(event);
  }

  @Async("stompWorker")
  @TransactionalEventListener
  public void handleDeleted(PrivateChannelDeletedEvent event) {
    for (UUID participantId : event.getParticipantIds()) {
      notifier.notifyDeleted(participantId, event);
    }
  }
}
