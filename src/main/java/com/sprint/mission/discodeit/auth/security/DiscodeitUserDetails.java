package com.sprint.mission.discodeit.auth.security;

import com.sprint.mission.discodeit.global.security.utils.DiscodeitAuthorityUtils;
import com.sprint.mission.discodeit.user.dto.response.UserResponse;
import java.util.Collection;
import lombok.Getter;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class DiscodeitUserDetails implements UserDetails, CredentialsContainer {

  @Getter
  private final UserResponse userResponse;
  private String password;
  private final Collection<GrantedAuthority> authorities;

  public DiscodeitUserDetails(UserResponse userResponse, String password) {
    this.userResponse = userResponse;
    this.password = password;
    this.authorities = DiscodeitAuthorityUtils.from(userResponse.role());
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
    return userResponse.username();
  }

  @Override
  public int hashCode() {
    return userResponse.username().hashCode();
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

  @Override
  public void eraseCredentials() {
    this.password = null;
  }
}
