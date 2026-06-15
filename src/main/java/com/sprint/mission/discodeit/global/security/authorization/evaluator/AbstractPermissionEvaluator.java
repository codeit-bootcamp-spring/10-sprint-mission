package com.sprint.mission.discodeit.global.security.authorization.evaluator;

import com.sprint.mission.discodeit.common.jpa.BaseEntity;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.Authentication;

@RequiredArgsConstructor
public abstract class AbstractPermissionEvaluator<T extends BaseEntity> {

  @Getter
  private final String domain;
  private final JpaRepository<T, UUID> repository;

  public boolean evaluate(Authentication auth, Object targetDomainObject, String permission) {
    if (!targetDomainObject.getClass().getSimpleName().equals(domain)) {
      return false;
    }

    @SuppressWarnings("unchecked")
    T entity = (T) targetDomainObject;
    return checkAccess(auth, entity, permission);
  }

  public boolean evaluateById(Authentication auth, Serializable targetId, String permission) {
    return repository.findById((UUID) targetId)
        .map(entity -> checkAccess(auth, entity, permission))
        .orElse(false);
  }

  private boolean checkAccess(Authentication auth, T entity, String permission) {
    if ("WRITE".equals(permission)) {
      return Objects.requireNonNull(entity.getId(), "Id is null")
          .toString().equals(auth.getName());
    }
    return false;
  }
}
