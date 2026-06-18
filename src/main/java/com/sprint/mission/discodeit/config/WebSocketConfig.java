package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.security.JwtAuthenticationChannelInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.messaging.access.intercept.AuthorizationChannelInterceptor;
import org.springframework.security.messaging.access.intercept.MessageMatcherDelegatingAuthorizationManager;
import org.springframework.security.messaging.context.SecurityContextChannelInterceptor;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker   //STOMP기반 WebSocket 메시징 활성화
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    private final JwtAuthenticationChannelInterceptor jwtAuthenticationChannelInterceptor;

    /// WebSocket 최초 연결 주소 설정.
    /// 클라이언트가 GET /ws-stomp 요청을 보내고, 요청 헤더에 WebSocket으로 업그레이드하고 싶다는 정보 있으면
    /// Spring이 WebSocket 연결 요청으로 처리한다.
    /// ex)  const socket = new SockJS("http://localhost:8080/ws-stomp");
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        /// 클라이언트가 연결할 WebSocket 엔드포인트 정의
        registry.addEndpoint("/ws")

                // CORS 허용
                // 다른 Origin에서도 WebSocket 허용
                // ex) localhost:8080, localhost:3000 연결허용.
                // 운영에서는 "https://my-service.com" 이런식으로 제한하는게 좋음.
                .setAllowedOriginPatterns("*")

                // 브라우저나 환경이 WebSocket을 제대로 지원하지않을때 대체 방식으로 연결하게 해주는 옵션.
                /// WebSocket 가능 → WebSocket 사용
                /// WebSocket 불가 → XHR Streaming, Long Polling 등으로 대체
                .withSockJS(); //SockJS fallback 지원
    }

    /// WebSocket 연결 이후 STOMP 메시지가 어디로 이동할지 경로 규칙을 정하는 설정.
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        /// 메시지 발행(Publish)경로 prefix
        /// 클라이언트가 서버의 Controller로 메시지 보낼때 /pub으로 시작해야한다.
        /// stompClient.send("/pub/chat.send" ...)
        /// Spring은 /pub으로 시작하네? -> Controller로 보내야겠다. -> /pub 제거 -> @MessageMapping("/chat.send")
        config.setApplicationDestinationPrefixes("/pub");

        /// 구독(Subscrib) 경로 prefix
        /// /sub 로 시작하는 경로는 Message Broker가 관리한다.
        /// 서버 -> 클라이언트
        config.enableSimpleBroker("/sub");
    }

    /// 클라이언트 -> 서버로 들어오는 STOMP 메시지 채널에 인터셉터를 등록하는 설정
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {

        /// CONNECT 요청시,
        /// (1) jwtAuthenticationChannelInterceptor: JWT 검증
        /// (2) SecurityContextChannelInterceptor
        /// 를 거쳐서 Controller로 향한다.
        registration.interceptors(
                jwtAuthenticationChannelInterceptor,
                /// SecurityContextHoler는 보통 현재 요청을 처리하는 스레드에만 저장된다.
                /// 로그인 HTTP요청에서 인증됐다고 해서 나중에 WebSocket 메시지를 처리하는 스레드까지 그 인증 정보가 자동으로 남아 있지는 않는다.
                /// jwtAuthenticationChannelInterceptor에서 반환된 인증객체를 Spring SecurityContex에 연결한다.
                new SecurityContextChannelInterceptor(),
                authorizationChannelInterceptor()
                );
    }

    /// 모든 inbound STOMP 메시지에 USER 역할을 요구.
    private AuthorizationChannelInterceptor authorizationChannelInterceptor() {
        /// AuthorizationManger: Spring Security의 인가 판단 객체
        AuthorizationManager<Message<?>> authorizationManager =
                MessageMatcherDelegatingAuthorizationManager.builder()
                        /// CONNECT,SUBSCRIBE, SEND, DISCONNECT 모두 규칙적용.
                        .anyMessage()
                        /// hasRole은 내부적으로 ROLE_ 접두사를 붙여 권한을 검사한다.
                        /// 사용자의 권한지 ROLE_USER라면 허용되고, 인증정보 없거나 필요한 역할 없다면
                        /// AuthorizationChannelInterceptor가 메시지 처리 거부.
                        .hasRole(Role.USER.name())
                        .build();

        return new AuthorizationChannelInterceptor(authorizationManager);
    }



}
