package com.sprint.mission.discodeit.auth.infra.security;

import com.sprint.mission.discodeit.auth.domain.entity.UserCredential;
import com.sprint.mission.discodeit.auth.domain.exception.AuthException;
import com.sprint.mission.discodeit.auth.domain.exception.UserCredentialErrorCode;
import com.sprint.mission.discodeit.auth.infra.repository.UserCredentialRepository;
import com.sprint.mission.discodeit.common.support.DomainServiceSupport;
import com.sprint.mission.discodeit.user.domain.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class DiscodeitUserDetailsService implements UserDetailsService {

  private final UserCredentialRepository repository;

  @Override
  @Transactional(readOnly = true)
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    UserCredential credential = DomainServiceSupport.getOrThrow(email,
        repository::findByUser_Email,
        value -> new AuthException(UserCredentialErrorCode.USERNAME_NOT_FOUND, "email", value));
    User user = credential.getUser();
    return DiscodeitUserDetails.builder()
        .userId(user.getId())
        .password(credential.getPassword())
        .role(user.getRole())
        .build();
  }

}
