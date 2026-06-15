package com.sprint.mission.discodeit.user.application.supplier;

import com.sprint.mission.discodeit.common.support.DomainServiceSupport;
import com.sprint.mission.discodeit.user.domain.entity.User;
import com.sprint.mission.discodeit.user.domain.exception.UserErrorCode;
import com.sprint.mission.discodeit.user.domain.exception.UserException;
import com.sprint.mission.discodeit.user.infra.repository.UserRepository;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UsernameReferenceSupplier {

  private final UserRepository repository;

  public User getProxyByUsername(String username) {
    return DomainServiceSupport.getOrThrow(username, repository::findByUsername,
        value -> new UserException(UserErrorCode.USERNAME_NOT_FOUND, Map.of("username", value)));
  }
}
