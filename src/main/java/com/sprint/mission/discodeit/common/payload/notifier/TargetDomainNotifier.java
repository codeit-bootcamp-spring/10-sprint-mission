package com.sprint.mission.discodeit.common.payload.notifier;

import java.util.UUID;

public interface TargetDomainNotifier {

  void notifyCreated(UUID targetId, Object payload);

  void notifyUpdated(UUID targetId, Object payload);

  void notifyDeleted(UUID targetId, Object payload);
}
