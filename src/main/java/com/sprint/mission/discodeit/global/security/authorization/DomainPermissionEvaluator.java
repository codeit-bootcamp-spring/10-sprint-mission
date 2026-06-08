package com.sprint.mission.discodeit.global.security.authorization;

import com.sprint.mission.discodeit.global.security.authorization.evaluator.AbstractPermissionEvaluator;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class DomainPermissionEvaluator implements PermissionEvaluator {

  private final Map<String, AbstractPermissionEvaluator<?>> evaluatorMap;

  public DomainPermissionEvaluator(List<AbstractPermissionEvaluator<?>> evaluators) {
    this.evaluatorMap = evaluators.stream().collect(
        Collectors.toMap(AbstractPermissionEvaluator::getDomain, e -> e));
  }

  @Override
  public boolean hasPermission(Authentication authentication, Object targetDomainObject,
      Object permission) {
    if (targetDomainObject == null) {
      return false;
    }

    AbstractPermissionEvaluator<?> evaluator = evaluatorMap.get(
        targetDomainObject.getClass().getSimpleName());
    return evaluator != null && evaluator.evaluate(authentication, targetDomainObject,
        (String) permission);
  }

  @Override
  public boolean hasPermission(Authentication authentication, Serializable targetId,
      String targetType, Object permission) {
    if (targetId == null || targetType == null) {
      return false;
    }

    AbstractPermissionEvaluator<?> evaluator = evaluatorMap.get(targetType);
    return evaluator != null && evaluator.evaluateById(authentication, targetId,
        (String) permission);
  }
}
