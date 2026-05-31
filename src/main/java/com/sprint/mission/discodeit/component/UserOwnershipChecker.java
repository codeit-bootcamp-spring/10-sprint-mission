package com.sprint.mission.discodeit.component;

import com.sprint.mission.discodeit.config.DiscodeitUserDetails;
import java.util.UUID;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class UserOwnershipChecker {

  public boolean isOwner(UUID userId, Authentication authentication) {
    DiscodeitUserDetails principal = (DiscodeitUserDetails) authentication.getPrincipal();
    return principal.getUserDto().id().equals(userId);
  }
}
