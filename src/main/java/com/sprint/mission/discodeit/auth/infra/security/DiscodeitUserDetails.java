package com.sprint.mission.discodeit.auth.infra.security;

import com.sprint.mission.discodeit.global.security.utils.DiscodeitAuthorityUtils;
import com.sprint.mission.discodeit.user.domain.entity.constant.UserRole;
import java.util.Collection;
import java.util.UUID;
import lombok.Builder;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class DiscodeitUserDetails implements UserDetails, CredentialsContainer {

  private final String userId;
  private String password;
  private final Collection<GrantedAuthority> authorities;

  @Builder
  public DiscodeitUserDetails(UUID userId, String password, UserRole role) {
    this.userId = userId.toString();
    this.password = password;
    this.authorities = DiscodeitAuthorityUtils.from(role);
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
    return userId;
  }

  @Override
  public int hashCode() {
    return userId.hashCode();
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
    return userId.equals(that.userId);
  }

  @Override
  public void eraseCredentials() {
    this.password = null;
  }
}
