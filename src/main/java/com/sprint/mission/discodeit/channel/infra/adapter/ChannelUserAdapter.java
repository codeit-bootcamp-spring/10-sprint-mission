package com.sprint.mission.discodeit.channel.infra.adapter;

import com.sprint.mission.discodeit.channel.domain.provider.ChannelUserResolver;
import com.sprint.mission.discodeit.common.reference.resolver.AbstractEntityReferenceResolver;
import com.sprint.mission.discodeit.common.reference.supplier.EntityReferenceSupplier;
import com.sprint.mission.discodeit.user.domain.entity.User;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class ChannelUserAdapter
    extends AbstractEntityReferenceResolver<User>
    implements ChannelUserResolver {

  public ChannelUserAdapter(
      EntityReferenceSupplier<User> service) {
    super(service);
  }

  @Override
  public List<User> getOrThrow(Collection<UUID> ids) {
    return service.getOrThrow(ids);
  }
}
