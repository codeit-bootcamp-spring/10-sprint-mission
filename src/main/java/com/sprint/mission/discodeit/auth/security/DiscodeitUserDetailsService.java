package com.sprint.mission.discodeit.auth.security;

import com.sprint.mission.discodeit.auth.entity.UserCredential;
import com.sprint.mission.discodeit.auth.repository.UserCredentialRepository;
import com.sprint.mission.discodeit.user.mapper.UserMapper;
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

  private final UserCredentialRepository userCredentialRepository;
  private final UserMapper userMapper;

  @Override
  @Transactional(readOnly = true)
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    UserCredential credential = userCredentialRepository.findByUser_Email(email)
        .orElseThrow(() -> new UsernameNotFoundException("not found user credentials"));
    return new DiscodeitUserDetails(userMapper.toDto(credential.getUser()),
        credential.getPassword());
  }

}
