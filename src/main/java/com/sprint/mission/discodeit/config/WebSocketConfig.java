package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.security.jwt.JwtAuthenticationChannelInterceptor;
import org.springframework.boot.actuate.security.AuthorizationAuditListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.security.messaging.access.intercept.AuthorizationChannelInterceptor;
import org.springframework.security.messaging.access.intercept.MessageMatcherDelegatingAuthorizationManager;
import org.springframework.security.messaging.context.SecurityContextChannelInterceptor;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

  private final JwtAuthenticationChannelInterceptor jwtAuthenticationChannelInterceptor;
  private final AuthorizationAuditListener authorizationAuditListener;

  public WebSocketConfig(
      JwtAuthenticationChannelInterceptor jwtAuthenticationChannelInterceptor,
      AuthorizationAuditListener authorizationAuditListener) {
    this.jwtAuthenticationChannelInterceptor = jwtAuthenticationChannelInterceptor;
    this.authorizationAuditListener = authorizationAuditListener;
  }

  @Override
  public void configureMessageBroker(MessageBrokerRegistry config) {
    // 메세지 구독시 prefix
    config.enableSimpleBroker("/sub");

    // 서버로 메세지 보낼때
    config.setApplicationDestinationPrefixes("/pub");
  }

  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    // STOMP 웹소켓 엔트포인트
    registry.addEndpoint("/ws").setAllowedOriginPatterns("*").withSockJS();
  }

  @Override
  public void configureClientInboundChannel(ChannelRegistration registration) {
    registration.interceptors(
        // CONNECT 프레임 JWT 인증
        jwtAuthenticationChannelInterceptor,

        // accessor의 인증 정보를 SecurityContext처럼 사용할 수 있게 연결
        new SecurityContextChannelInterceptor(),

        // WebSocket 메시지 인가
        authorizationChannelInterceptor());
  }

  private AuthorizationChannelInterceptor authorizationChannelInterceptor() {
    return new AuthorizationChannelInterceptor(
        MessageMatcherDelegatingAuthorizationManager.builder()
            // WebSocket으로 들어오는 모든 메시지는 USER 이상 권한 필요
            .anyMessage()
            .hasRole(Role.USER.name())
            .build());
  }
}
