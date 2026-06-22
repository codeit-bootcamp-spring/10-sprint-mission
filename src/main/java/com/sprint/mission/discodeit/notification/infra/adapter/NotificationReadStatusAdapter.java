package com.sprint.mission.discodeit.notification.infra.adapter;

import com.sprint.mission.discodeit.common.reference.resolver.AbstractEntityReferenceResolver;
import com.sprint.mission.discodeit.common.reference.supplier.EntityReferenceSupplier;
import com.sprint.mission.discodeit.notification.domain.provider.NotificationReadStatusResolver;
import com.sprint.mission.discodeit.readstatus.application.supplier.ReadStatusReferenceQuerySupplier;
import com.sprint.mission.discodeit.readstatus.domain.entity.ReadStatus;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class NotificationReadStatusAdapter
    extends AbstractEntityReferenceResolver<ReadStatus>
    implements NotificationReadStatusResolver {

  private final ReadStatusReferenceQuerySupplier querySupplier;

  public NotificationReadStatusAdapter(
      EntityReferenceSupplier<ReadStatus> service,
      ReadStatusReferenceQuerySupplier querySupplier) {
    super(service);
    this.querySupplier = querySupplier;
  }

  @Override
  public List<UUID> findUserIdsByChannelIdAndNotificationEnabledIsTrue(UUID channelId) {
    return querySupplier.findUserIdsByChannelIdAndNotificationEnabledIsTrue(channelId);
  }
}
