package com.sprint.mission.discodeit.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

  private final WebSocketAuthChannelInterceptor webSocketAuthChannelInterceptor;

  @Override
  public void configureMessageBroker(MessageBrokerRegistry config) {

    config.enableSimpleBroker("/sub"); // 클라이언트가 메시지를 구독할 때 사용하는 destination prefix
    config.setApplicationDestinationPrefixes("/pub"); // 클라이언트가 메시지를 발행할 때 사용하는 prefix
  }

  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    // WebSocket handshake endpoint
    // 실제 STOMP 발행 destination은 /pub/messages이고, 연결 시작점은 /ws
    registry.addEndpoint("/ws")
        .setAllowedOriginPatterns("http://localhost:3000")
        .withSockJS();
  }

  @Override
  public void configureClientInboundChannel(ChannelRegistration registration) {
    // 클라이언트 -> 서버로 들어오는 STOMP 메시지를 가로채 인증 처리
    registration.interceptors(webSocketAuthChannelInterceptor);
  }

}
