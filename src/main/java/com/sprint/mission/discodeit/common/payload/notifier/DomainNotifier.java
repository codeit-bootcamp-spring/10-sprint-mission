package com.sprint.mission.discodeit.common.payload.notifier;

public interface DomainNotifier {

  void notifyCreated(Object payload);

  void notifyUpdated(Object payload);

  void notifyDeleted(Object payload);
}
