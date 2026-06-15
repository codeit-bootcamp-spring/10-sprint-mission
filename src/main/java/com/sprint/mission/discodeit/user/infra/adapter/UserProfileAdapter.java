package com.sprint.mission.discodeit.user.infra.adapter;

import com.sprint.mission.discodeit.binarycontent.domain.entity.BinaryContent;
import com.sprint.mission.discodeit.common.reference.resolver.AbstractEntityReferenceResolver;
import com.sprint.mission.discodeit.common.reference.supplier.EntityReferenceSupplier;
import com.sprint.mission.discodeit.user.domain.provider.UserProfileResolver;
import org.springframework.stereotype.Component;

@Component
public class UserProfileAdapter
    extends AbstractEntityReferenceResolver<BinaryContent>
    implements UserProfileResolver {

  public UserProfileAdapter(EntityReferenceSupplier<BinaryContent> service) {
    super(service);
  }
}
