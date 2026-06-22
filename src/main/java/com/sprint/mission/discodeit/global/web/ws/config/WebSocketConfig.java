package com.sprint.mission.discodeit.global.web.ws.config;

import com.sprint.mission.discodeit.global.web.ws.constant.WebSocketConstants;
import com.sprint.mission.discodeit.global.web.ws.stomp.interceptor.StompInterceptor;
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
class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
  private final StompInterceptor stompInterceptor;

  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    registry.addEndpoint(WebSocketConstants.ENDPOINT)
            .setAllowedOriginPatterns("*")
            .withSockJS();
  }

  @Override
  public void configureMessageBroker(MessageBrokerRegistry registry) {
    registry.enableSimpleBroker(WebSocketConstants.SUB_PREFIX);
    registry.setApplicationDestinationPrefixes(WebSocketConstants.PUB_PREFIX);
  }

  @Override
  public void configureClientInboundChannel(ChannelRegistration registration) {
    registration.interceptors(stompInterceptor);
  }
}
