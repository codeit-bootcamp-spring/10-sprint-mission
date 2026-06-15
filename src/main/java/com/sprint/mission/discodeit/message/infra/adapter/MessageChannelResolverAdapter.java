package com.sprint.mission.discodeit.message.infra.adapter;

import com.sprint.mission.discodeit.channel.domain.entity.Channel;
import com.sprint.mission.discodeit.common.reference.resolver.AbstractEntityReferenceResolver;
import com.sprint.mission.discodeit.common.reference.supplier.EntityReferenceSupplier;
import com.sprint.mission.discodeit.message.domain.provider.MessageChannelResolver;
import org.springframework.stereotype.Component;

@Component
public class MessageChannelResolverAdapter
    extends AbstractEntityReferenceResolver<Channel>
    implements MessageChannelResolver {

  public MessageChannelResolverAdapter(
      EntityReferenceSupplier<Channel> service) {
    super(service);
  }
}
