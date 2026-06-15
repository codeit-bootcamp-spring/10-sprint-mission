package com.sprint.mission.discodeit.readstatus.infra.adapter;

import com.sprint.mission.discodeit.common.reference.resolver.AbstractEntityReferenceResolver;
import com.sprint.mission.discodeit.common.reference.supplier.EntityReferenceSupplier;
import com.sprint.mission.discodeit.readstatus.domain.provider.ReadStatusUserResolver;
import com.sprint.mission.discodeit.user.domain.entity.User;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class ReadStatusUserAdapter
    extends AbstractEntityReferenceResolver<User>
    implements ReadStatusUserResolver {

  public ReadStatusUserAdapter(
      EntityReferenceSupplier<User> service) {
    super(service);
  }

  @Override
  public List<User> getProxy(List<UUID> ids) {
    return service.getProxy(ids);
  }
}
