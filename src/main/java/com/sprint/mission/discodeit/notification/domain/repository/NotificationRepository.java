package com.sprint.mission.discodeit.notification.domain.repository;

import com.sprint.mission.discodeit.common.jpa.repository.DomainRepository;
import com.sprint.mission.discodeit.notification.domain.entity.Notification;
import java.util.List;
import java.util.UUID;

public interface NotificationRepository extends DomainRepository<Notification> {

  List<Notification> findAllByReceiver_Id(UUID receiverId);
}
