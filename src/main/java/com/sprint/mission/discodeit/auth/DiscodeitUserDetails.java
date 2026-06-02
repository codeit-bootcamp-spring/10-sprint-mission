package com.sprint.mission.discodeit.auth;

import com.sprint.mission.discodeit.dto.UserDto;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
public class DiscodeitUserDetails implements UserDetails {

  private final UserDto.Response userDto;
  private final String username;
  private final String password;

  public DiscodeitUserDetails(UserDto.Response userDto, String password) {
    this.userDto = userDto;
    this.password = password;
    this.username = userDto.username();
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of(new SimpleGrantedAuthority("ROLE_" + userDto.role().name()));
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof DiscodeitUserDetails that)) {
      return false;
    }
    return Objects.equals(this.userDto.id(), that.userDto.id());
  }

  @Override
  public int hashCode() {
    return Objects.hash(userDto.id());
  }
}
