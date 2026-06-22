package com.sprint.mission.discodeit.message.infra.adapter;

import com.sprint.mission.discodeit.binarycontent.domain.entity.BinaryContent;
import com.sprint.mission.discodeit.common.reference.resolver.AbstractEntityReferenceResolver;
import com.sprint.mission.discodeit.common.reference.supplier.EntityReferenceSupplier;
import com.sprint.mission.discodeit.message.domain.provider.MessageBinaryContentResolver;
import org.springframework.stereotype.Component;

@Component
public class MessageBinaryContentResolverAdapter
    extends AbstractEntityReferenceResolver<BinaryContent>
    implements MessageBinaryContentResolver {

  public MessageBinaryContentResolverAdapter(
      EntityReferenceSupplier<BinaryContent> service) {
    super(service);
  }
}
