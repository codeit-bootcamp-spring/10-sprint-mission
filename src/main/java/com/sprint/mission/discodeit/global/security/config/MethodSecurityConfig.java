package com.sprint.mission.discodeit.global.security.config;

import com.sprint.mission.discodeit.global.security.authorization.DomainPermissionEvaluator;
import com.sprint.mission.discodeit.user.entity.constant.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.core.GrantedAuthorityDefaults;

@Configuration
@RequiredArgsConstructor
public class MethodSecurityConfig {

  private final DomainPermissionEvaluator permissionEvaluator;

  @Bean
  public GrantedAuthorityDefaults grantedAuthorityDefaults() {
    return new GrantedAuthorityDefaults("");
  }

  @Bean
  public RoleHierarchy roleHierarchy() {
    return RoleHierarchyImpl.withRolePrefix("")
        .role(UserRole.ADMIN.name()).implies(UserRole.CHANNEL_MANAGER.name())
        .role(UserRole.CHANNEL_MANAGER.name()).implies(UserRole.USER.name())
        .build();
  }

  @Bean
  public MethodSecurityExpressionHandler methodSecurityExpressionHandler(
      RoleHierarchy roleHierarchy) {
    DefaultMethodSecurityExpressionHandler handler = new DefaultMethodSecurityExpressionHandler();
    handler.setRoleHierarchy(roleHierarchy);
    handler.setDefaultRolePrefix("");
    handler.setPermissionEvaluator(permissionEvaluator);
    return handler;
  }

}
