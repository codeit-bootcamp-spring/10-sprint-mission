package com.sprint.mission.discodeit.message.infra.adapter;

import com.sprint.mission.discodeit.common.reference.resolver.AbstractEntityReferenceResolver;
import com.sprint.mission.discodeit.common.reference.supplier.EntityReferenceSupplier;
import com.sprint.mission.discodeit.message.domain.provider.MessageUserResolver;
import com.sprint.mission.discodeit.user.domain.entity.User;
import org.springframework.stereotype.Component;

@Component
public class MessageUserResolverAdapter
    extends AbstractEntityReferenceResolver<User>
    implements MessageUserResolver {

  public MessageUserResolverAdapter(
      EntityReferenceSupplier<User> service) {
    super(service);
  }
}
