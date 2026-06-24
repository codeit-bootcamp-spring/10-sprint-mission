package com.sprint.mission.discodeit.config.websocket;

import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.security.interceptor.JwtAuthenticationChannelInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.security.messaging.access.intercept.AuthorizationChannelInterceptor;
import org.springframework.security.messaging.access.intercept.MessageMatcherDelegatingAuthorizationManager;
import org.springframework.security.messaging.context.SecurityContextChannelInterceptor;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

// WebSocket/STOMP 연결 엔드포인트와 메시지 브로커 prefix 설정 클래스
@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtAuthenticationChannelInterceptor jwtAuthenticationChannelInterceptor;

    // 클라이언트가 최초 WebSocket/STOMP 연결을 맺을 엔드포인트를 등록
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 클라이언트가 SockJS를 통해 접속할 STOMP 엔드포인트를 /ws로 등록
        // 모든 Origin 허용
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS();

    }

    // 구독용 prefix(/sub)와 발행용 prefix(/pub)를 메시지 브로커에 설정
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // /sub으로 시작하는 destination은 SimpleBroker가 구독자에게 메시지를 전달하도록 설정
        registry.enableSimpleBroker("/sub");
        // /pub으로 시작하는 destination은 서버의 @MessageMapping Controller로 라우팅되도록 설정
        registry.setApplicationDestinationPrefixes("/pub");
    }

    // 클라이언트에서 서버로 들어오는 STOMP 메시지 채널에 인터셉터 추가
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(
                jwtAuthenticationChannelInterceptor,
                new SecurityContextChannelInterceptor(),
                authorizationChannelInterceptor()
        );
    }

    private AuthorizationChannelInterceptor authorizationChannelInterceptor() {
        return new AuthorizationChannelInterceptor(
                MessageMatcherDelegatingAuthorizationManager.builder()
                        .anyMessage().hasAnyRole(
                                Role.USER.name(),
                                Role.CHANNEL_MANAGER.name(),
                                Role.ADMIN.name()
                        )
                        .build()
        );
    }
}
