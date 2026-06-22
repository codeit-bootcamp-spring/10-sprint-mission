package com.sprint.mission.discodeit.channel.application.supplier;

import com.sprint.mission.discodeit.channel.domain.entity.Channel;
import com.sprint.mission.discodeit.channel.domain.exception.ChannelErrorCode;
import com.sprint.mission.discodeit.channel.domain.exception.ChannelException;
import com.sprint.mission.discodeit.common.exception.DiscodeitException;
import com.sprint.mission.discodeit.common.jpa.repository.EntityReferenceJpaRepository;
import com.sprint.mission.discodeit.common.reference.supplier.AbstractEntityReferenceSupplier;
import java.util.Collection;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ChannelReferenceSupplier extends AbstractEntityReferenceSupplier<Channel> {

  public ChannelReferenceSupplier(EntityReferenceJpaRepository<Channel> repository) {
    super(repository);
  }

  @Override
  protected DiscodeitException notFoundException(UUID id) {
    return new ChannelException(ChannelErrorCode.CHANNELID_NOT_FOUND, id);
  }

  @Override
  protected DiscodeitException notFoundException(Collection<UUID> ids) {
    return new ChannelException(ChannelErrorCode.CHANNELID_NOT_FOUND, null);
  }
}
