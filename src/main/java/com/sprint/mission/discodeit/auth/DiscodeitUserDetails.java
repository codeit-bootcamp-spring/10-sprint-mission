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

/**
 * 스프링 시큐리티 인증 객체(Authentication)에 담길 사용자 상세 정보입니다.
 */
@Getter
public class DiscodeitUserDetails implements UserDetails {

  private final UserDto.Response userDto; // 서비스 계층에서 사용할 DTO
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

  // ID 기반으로 동일 사용자 판단 (세션 관리용)
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof DiscodeitUserDetails that)) return false;
    return Objects.equals(this.userDto.id(), that.userDto.id());
  }

  @Override
  public int hashCode() {
    return Objects.hash(userDto.id());
  }
}
