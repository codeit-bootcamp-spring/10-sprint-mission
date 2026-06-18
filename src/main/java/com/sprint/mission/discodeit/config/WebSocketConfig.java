package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.auth.JwtAuthenticationChannelInterceptor;
import com.sprint.mission.discodeit.auth.enums.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.authorization.AuthorityAuthorizationManager;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.annotation.web.socket.EnableWebSocketSecurity;
import org.springframework.security.messaging.access.expression.DefaultMessageSecurityExpressionHandler;
import org.springframework.security.messaging.access.intercept.AuthorizationChannelInterceptor;
import org.springframework.security.messaging.access.intercept.MessageAuthorizationContext;
import org.springframework.security.messaging.access.intercept.MessageMatcherDelegatingAuthorizationManager;
import org.springframework.security.messaging.context.SecurityContextChannelInterceptor;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

  private final JwtAuthenticationChannelInterceptor jwtAuthenticationChannelInterceptor;
  private final RoleHierarchy roleHierarchy;

  @Override
  public void configureMessageBroker(MessageBrokerRegistry config) {
    config.enableSimpleBroker("/sub");
    config.setApplicationDestinationPrefixes("/pub");
  }

  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    registry.addEndpoint("/ws")
        .setAllowedOrigins("http://localhost:3000", "http://localhost:8080", "http://localhost:80")
        .withSockJS();
  }

  @Override
  public void configureClientInboundChannel(ChannelRegistration registration) {
    registration.interceptors(
        jwtAuthenticationChannelInterceptor,
        new SecurityContextChannelInterceptor(),
        authorizationChannelInterceptor()
    );
  }

  private AuthorizationChannelInterceptor authorizationChannelInterceptor() {
//    return new AuthorizationChannelInterceptor(
//        MessageMatcherDelegatingAuthorizationManager.builder()
//            .anyMessage().hasRole(Role.USER.name())
//            .build()
//
//    );//역할 계층이 적용 안됨

    MessageMatcherDelegatingAuthorizationManager.Builder builder = MessageMatcherDelegatingAuthorizationManager.builder();

    // 계층을 적용한 권한 매니저 적용
    AuthorizationManager<MessageAuthorizationContext<?>> userManager = createHierarchyManager(
        Role.USER.name());

    builder
        .anyMessage().access(userManager);

    return new AuthorizationChannelInterceptor(builder.build());
  }

  //권한 매니저 생성
  private AuthorizationManager<MessageAuthorizationContext<?>> createHierarchyManager(String role) {

    AuthorityAuthorizationManager<Message<?>> delegate = AuthorityAuthorizationManager.hasRole(
        role);
    delegate.setRoleHierarchy(roleHierarchy); // 계층 적용

    return (authentication, context) ->
        (org.springframework.security.authorization.AuthorizationDecision) delegate.authorize(
            authentication, context.getMessage());
  }

}
