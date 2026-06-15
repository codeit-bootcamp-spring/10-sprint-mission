package com.sprint.mission.discodeit.notification.infra.adapter;

import com.sprint.mission.discodeit.channel.domain.entity.Channel;
import com.sprint.mission.discodeit.common.reference.resolver.AbstractEntityReferenceResolver;
import com.sprint.mission.discodeit.common.reference.supplier.EntityReferenceSupplier;
import com.sprint.mission.discodeit.notification.domain.provider.NotificationChannelResolver;
import java.util.UUID;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class NotificationChannelAdapter
    extends AbstractEntityReferenceResolver<Channel>
    implements NotificationChannelResolver {

  public NotificationChannelAdapter(
      EntityReferenceSupplier<Channel> service) {
    super(service);
  }

  @Override
  public String getChannelName(UUID id) {
    String name = service.getOrThrow(id).getName();
    if (StringUtils.hasText(name)) {
      return name;
    }
    return "anonymous";
  }
}
