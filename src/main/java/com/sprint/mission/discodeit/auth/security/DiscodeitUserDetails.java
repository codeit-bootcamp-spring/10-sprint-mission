package com.sprint.mission.discodeit.auth.security;

import com.sprint.mission.discodeit.auth.utils.DiscodeitAuthorityUtils;
import com.sprint.mission.discodeit.user.dto.response.UserResponse;
import java.util.Collection;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class DiscodeitUserDetails implements UserDetails {

  @Getter
  private final UserResponse userResponse;
  private final String password;
  private final Collection<GrantedAuthority> authorities;

  public DiscodeitUserDetails(UserResponse userResponse, String password) {
    this.userResponse = userResponse;
    this.password = password;
    this.authorities = DiscodeitAuthorityUtils.toGrantedAuthorities(userResponse.role());
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return authorities;
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public String getUsername() {
    return userResponse.id().toString();
  }

  @Override
  public int hashCode() {
    return userResponse.id().hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    DiscodeitUserDetails that = (DiscodeitUserDetails) obj;
    return userResponse.id().equals(that.userResponse.id());
  }
}
