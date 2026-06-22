package com.sprint.mission.discodeit.readstatus.application.supplier;

import com.sprint.mission.discodeit.common.exception.DiscodeitException;
import com.sprint.mission.discodeit.common.jpa.repository.EntityReferenceJpaRepository;
import com.sprint.mission.discodeit.common.reference.supplier.AbstractEntityReferenceSupplier;
import com.sprint.mission.discodeit.readstatus.domain.entity.ReadStatus;
import com.sprint.mission.discodeit.readstatus.domain.exception.ReadStatusErrorCode;
import com.sprint.mission.discodeit.readstatus.domain.exception.ReadStatusException;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ReadStatusReferenceSupplier extends AbstractEntityReferenceSupplier<ReadStatus> {

  public ReadStatusReferenceSupplier(
      EntityReferenceJpaRepository<ReadStatus> repository) {
    super(repository);
  }

  @Override
  protected DiscodeitException notFoundException(UUID id) {
    return new ReadStatusException(ReadStatusErrorCode.READSTATUSID_NOT_FOUND, id);
  }

  @Override
  protected DiscodeitException notFoundException(Collection<UUID> ids) {
    return new ReadStatusException(ReadStatusErrorCode.READSTATUSID_NOT_FOUND,
        Map.of("readStatusIds", ids));
  }

}
