package com.sprint.mission.discodeit.auth.application.service;

import com.sprint.mission.discodeit.auth.domain.entity.UserCredential;
import com.sprint.mission.discodeit.auth.domain.exception.AuthException;
import com.sprint.mission.discodeit.auth.domain.exception.UserCredentialErrorCode;
import com.sprint.mission.discodeit.auth.domain.provider.AuthUserResolver;
import com.sprint.mission.discodeit.auth.infra.repository.UserCredentialRepository;
import com.sprint.mission.discodeit.common.support.DomainServiceSupport;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserCredentialService {

  private final UserCredentialRepository repository;
  private final PasswordEncoder passwordEncoder;
  private final AuthUserResolver userProvider;

  public void create(UUID userId, String password) {
    UserCredential credential = UserCredential.builder()
        .user(userProvider.getProxy(userId))
        .password(passwordEncoder.encode(password))
        .build();
    repository.save(credential);
  }

  public void update(UUID userId, String password) {
    UserCredential credential = findByUserId(userId);
    credential.updatePassword(passwordEncoder.encode(password));
  }

  private UserCredential findByUserId(UUID id) {
    return DomainServiceSupport.getOrThrow(id, repository::findByUser_Id,
        value -> new AuthException(
            UserCredentialErrorCode.USER_CREDENTIAL_NOT_FOUND, "userId", value));
  }
}
