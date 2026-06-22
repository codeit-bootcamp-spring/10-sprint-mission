package com.sprint.mission.discodeit.user.infra.repository;

import com.sprint.mission.discodeit.common.jpa.repository.EntityReferenceJpaRepository;
import com.sprint.mission.discodeit.user.domain.entity.User;
import com.sprint.mission.discodeit.user.domain.repository.UserQueryReferenceRepository;

public interface UserQueryReferenceJpaRepository extends UserQueryReferenceRepository,
    EntityReferenceJpaRepository<User> {

}
