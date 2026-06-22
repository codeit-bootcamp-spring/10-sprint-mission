package com.sprint.mission.discodeit.binarycontent.application.supplier;

import com.sprint.mission.discodeit.binarycontent.domain.entity.BinaryContent;
import com.sprint.mission.discodeit.binarycontent.domain.exception.BinaryContentErrorCode;
import com.sprint.mission.discodeit.binarycontent.domain.exception.BinaryContentException;
import com.sprint.mission.discodeit.common.exception.DiscodeitException;
import com.sprint.mission.discodeit.common.jpa.repository.EntityReferenceJpaRepository;
import com.sprint.mission.discodeit.common.reference.supplier.AbstractEntityReferenceSupplier;
import java.util.Collection;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class BinaryContentReferenceSupplier extends AbstractEntityReferenceSupplier<BinaryContent> {

  public BinaryContentReferenceSupplier(
      EntityReferenceJpaRepository<BinaryContent> repository) {
    super(repository);
  }

  @Override
  protected DiscodeitException notFoundException(UUID id) {
    return new BinaryContentException(BinaryContentErrorCode.BINARYCONTENTID_NOT_FOUND, id);
  }

  @Override
  protected DiscodeitException notFoundException(Collection<UUID> ids) {
    return new BinaryContentException(BinaryContentErrorCode.BINARYCONTENTID_NOT_FOUND, null);
  }
}
