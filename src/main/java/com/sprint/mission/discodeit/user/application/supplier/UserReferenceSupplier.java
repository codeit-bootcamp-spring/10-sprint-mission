package com.sprint.mission.discodeit.user.application.supplier;

import com.sprint.mission.discodeit.common.exception.DiscodeitException;
import com.sprint.mission.discodeit.common.jpa.repository.EntityReferenceJpaRepository;
import com.sprint.mission.discodeit.common.reference.supplier.AbstractEntityReferenceSupplier;
import com.sprint.mission.discodeit.user.domain.entity.User;
import com.sprint.mission.discodeit.user.domain.exception.UserErrorCode;
import com.sprint.mission.discodeit.user.domain.exception.UserException;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class UserReferenceSupplier extends AbstractEntityReferenceSupplier<User> {

  public UserReferenceSupplier(
      EntityReferenceJpaRepository<User> repository) {
    super(repository);
  }

  @Override
  protected DiscodeitException notFoundException(UUID id) {
    return new UserException(UserErrorCode.USERID_NOT_FOUND, id);
  }

  @Override
  protected DiscodeitException notFoundException(Collection<UUID> ids) {
    return new UserException(UserErrorCode.USERID_NOT_FOUND, Map.of("id", ids));
  }
}
