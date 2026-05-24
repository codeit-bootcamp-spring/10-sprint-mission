package com.sprint.mission.discodeit.global.security.authorization.evaluator;

import com.sprint.mission.discodeit.user.entity.User;
import com.sprint.mission.discodeit.user.repository.UserRepository;
import org.springframework.stereotype.Component;

@Component
public class UserPermissionEvaluator extends AbstractPermissionEvaluator<User> {

  public UserPermissionEvaluator(UserRepository repository) {
    super("User", repository);
  }
}
