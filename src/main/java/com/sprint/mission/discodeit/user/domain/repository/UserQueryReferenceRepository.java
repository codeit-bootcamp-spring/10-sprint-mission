package com.sprint.mission.discodeit.user.domain.repository;

import com.sprint.mission.discodeit.user.domain.entity.User;
import com.sprint.mission.discodeit.user.domain.entity.constant.UserRole;
import java.util.Optional;

public interface UserQueryReferenceRepository {

  Optional<User> findByUsername(String username);

  boolean existsByRole(UserRole role);
}
