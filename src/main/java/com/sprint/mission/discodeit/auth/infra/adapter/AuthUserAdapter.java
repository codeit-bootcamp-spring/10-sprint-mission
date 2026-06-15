package com.sprint.mission.discodeit.auth.infra.adapter;

import com.sprint.mission.discodeit.auth.domain.provider.AuthUserResolver;
import com.sprint.mission.discodeit.common.reference.resolver.AbstractEntityReferenceResolver;
import com.sprint.mission.discodeit.common.reference.supplier.EntityReferenceSupplier;
import com.sprint.mission.discodeit.user.domain.entity.User;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class AuthUserAdapter
    extends AbstractEntityReferenceResolver<User>
    implements AuthUserResolver {

  public AuthUserAdapter(EntityReferenceSupplier<User> service) {
    super(service);
  }

  @Override
  public User getProxy(UUID id) {
    return service.getProxy(id);
  }

  @Override
  public User getOrThrow(UUID id) {
    return service.getOrThrow(id);
  }
}
