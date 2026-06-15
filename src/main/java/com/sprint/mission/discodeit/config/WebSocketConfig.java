package com.sprint.mission.discodeit.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker   //STOMP기반 WebSocket 메시징 활성화
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
//    private final StompChannelInterceptor interceptor;

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

//    @Override
//    public void configureClientInboundChannel(ChannelRegistration registry) {
//        registry.interceptors(interceptor);
//    }



}
