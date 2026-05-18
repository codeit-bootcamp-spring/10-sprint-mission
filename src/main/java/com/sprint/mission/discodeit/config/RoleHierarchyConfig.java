package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.entity.Role;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;

@Configuration
public class RoleHierarchyConfig {

  @Bean
  public RoleHierarchy roleHierarchy() {
    return RoleHierarchyImpl.fromHierarchy(
        Role.ADMIN.getAuthority() + " > " + Role.CHANNEL_MANAGER.getAuthority() + " > "
            + Role.USER.getAuthority());
  }
}
