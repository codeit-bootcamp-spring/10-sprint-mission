package com.sprint.mission.discodeit.auth.utils;

import com.sprint.mission.discodeit.auth.security.DiscodeitGrantedAuthority;
import com.sprint.mission.discodeit.user.entity.constant.UserRole;
import java.util.Set;
import org.springframework.security.core.GrantedAuthority;

public final class DiscodeitAuthorityUtils {

  public static Set<GrantedAuthority> toGrantedAuthorities(UserRole role) {
    return Set.of(new DiscodeitGrantedAuthority(role));
  }
}
