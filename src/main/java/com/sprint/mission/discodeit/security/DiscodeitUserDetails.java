package com.sprint.mission.discodeit.security;

import com.sprint.mission.discodeit.dto.user.UserDto;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
@RequiredArgsConstructor
public class DiscodeitUserDetails implements UserDetails {

  private final UserDto userDto;
  private final String password;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    // Spring Security 표준을 맞추기 위해 'ROLE_' 접두사를 붙여서 반환
    return List.of(new SimpleGrantedAuthority("ROLE_" + userDto.role().name()));
  }

  @Override
  public String getPassword() {
    return this.password;
  }

  @Override
  public String getUsername() {
    return userDto.username(); // 사용자 식별자
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }
}
