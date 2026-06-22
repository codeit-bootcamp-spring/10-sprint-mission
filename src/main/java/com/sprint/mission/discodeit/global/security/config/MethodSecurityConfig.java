package com.sprint.mission.discodeit.global.security.config;

import com.sprint.mission.discodeit.global.security.authorization.DomainPermissionEvaluator;
import com.sprint.mission.discodeit.user.domain.entity.constant.UserRole;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.core.GrantedAuthorityDefaults;

@Configuration
@EnableMethodSecurity
public class MethodSecurityConfig {

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
      RoleHierarchy roleHierarchy, DomainPermissionEvaluator permissionEvaluator) {
    DefaultMethodSecurityExpressionHandler handler = new DefaultMethodSecurityExpressionHandler();
    handler.setRoleHierarchy(roleHierarchy);
    handler.setDefaultRolePrefix("");
    handler.setPermissionEvaluator(permissionEvaluator);
    return handler;
  }

}
