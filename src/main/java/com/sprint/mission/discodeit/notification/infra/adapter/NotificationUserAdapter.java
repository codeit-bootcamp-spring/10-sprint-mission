package com.sprint.mission.discodeit.notification.infra.adapter;

import com.sprint.mission.discodeit.common.reference.resolver.AbstractEntityReferenceResolver;
import com.sprint.mission.discodeit.common.reference.supplier.EntityReferenceSupplier;
import com.sprint.mission.discodeit.notification.domain.provider.NotificationUserResolver;
import com.sprint.mission.discodeit.user.application.supplier.UsernameReferenceSupplier;
import com.sprint.mission.discodeit.user.domain.entity.User;
import java.util.UUID;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class NotificationUserAdapter
    extends AbstractEntityReferenceResolver<User>
    implements NotificationUserResolver {

  private final UsernameReferenceSupplier usernameReferenceSupplier;

  public NotificationUserAdapter(
      EntityReferenceSupplier<User> service,
      UsernameReferenceSupplier usernameReferenceSupplier) {
    super(service);
    this.usernameReferenceSupplier = usernameReferenceSupplier;
  }

  @Override
  public String getUsername(UUID id) {
    return service.getOrThrow(id).getUsername();
  }

  @Override
  public User getProxyByUsername(String username) {
    return usernameReferenceSupplier.getProxyByUsername(username);
  }
}
