package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.dto.userdto.UserDto;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
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
    return List.of(new SimpleGrantedAuthority("ROLE_" + userDto.role().name()));
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public String getUsername() {
    return userDto.username();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) { // 같은 객체면 True 반환
      return true;
    }
    if (!(o instanceof DiscodeitUserDetails that)) { // o가 DiscodeitUserDetails 객체가 아니면 False
      return false;
    }
    return Objects.equals(userDto.id(), that.userDto.id()); // 두 객체의 userDto.id를 비교
  }

  @Override
  public int hashCode() {
    return Objects.hash(userDto.id());
  } // userDto.id를 기반으로 해쉬를 반환
}
