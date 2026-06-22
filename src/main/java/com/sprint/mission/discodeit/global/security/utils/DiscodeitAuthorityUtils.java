package com.sprint.mission.discodeit.global.security.utils;

import com.sprint.mission.discodeit.auth.infra.security.DiscodeitGrantedAuthority;
import com.sprint.mission.discodeit.user.domain.entity.constant.UserRole;
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

  public static Collection<? extends GrantedAuthority> deserialize(String authorities) {
    return Arrays.stream(authorities.split(DELIMITER))
        .map(String::toUpperCase)
        .map(UserRole::valueOf)
        .map(DiscodeitGrantedAuthority::new)
        .collect(Collectors.toList());
  }
}
