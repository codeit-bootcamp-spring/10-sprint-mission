package com.sprint.mission.discodeit.global.security.utils;

import com.sprint.mission.discodeit.auth.security.DiscodeitGrantedAuthority;
import com.sprint.mission.discodeit.user.entity.constant.UserRole;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DiscodeitAuthorityUtils {

  private static final String DELIMITER = ",";

  public static Set<GrantedAuthority> from(UserRole role) {
    return Set.of(new DiscodeitGrantedAuthority(role));
  }

  public static String serializeAuthorities(Collection<? extends GrantedAuthority> authorities) {
    return authorities.stream()
        .map(GrantedAuthority::getAuthority)
        .collect(Collectors.joining(DELIMITER));
  }

  public static Collection<? extends GrantedAuthority> deserializeAuthorities(String authorities) {
    return Arrays.stream(authorities.split(DELIMITER))
        .map(String::toUpperCase)
        .map(UserRole::valueOf)
        .map(DiscodeitGrantedAuthority::new)
        .collect(Collectors.toList());
  }
}
