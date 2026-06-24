package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.entity.UserRole;
import com.sprint.mission.discodeit.security.StompAuthChannelInterceptor;
import com.sprint.mission.discodeit.security.StompErrorHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.authorization.AuthorityAuthorizationManager;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.messaging.access.intercept.AuthorizationChannelInterceptor;
import org.springframework.security.messaging.access.intercept.MessageAuthorizationContext;
import org.springframework.security.messaging.access.intercept.MessageMatcherDelegatingAuthorizationManager;
import org.springframework.security.messaging.context.SecurityContextChannelInterceptor;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/*
메시지 브로커 구성 - 구독/발행 prefix 지정 및 인메모리 브로커 활성화
STOMP 엔드포인트 등록 - 클라이언트가 WebSocket 연결을 맺는 진입점 정의
인바운드 채널 인터셉터 등록 - 수신 메시지에 대한 JWT 인증 및 SecurityContext 전파

클라이언트 발행 (/pub/***)
  -> 인바운드 채널 인터셉터(JWT 인증 -> SecurityContext 전파)
  -> @MessageMapping 핸들러
  -> SimpleBroker (/sub/***)
  -> 구독 중인 클라이언트들에게 전달
 */
@Configuration
@EnableWebSocketMessageBroker // STOMP 기반 메시지 브로커를 활성화
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

  // JWT 인증 처리를 담당하는 커스텀 STOMP 채널 인터셉터.
  // CONNECT 프레임 수신 시 헤더의 JWT 토큰을 검증하고 Principal을 세팅
  private final StompAuthChannelInterceptor stompAuthChannelInterceptor;
  // HTTP 시큐리티와 동일한 역할 계층(ADMIN -> CHANNEL_MANAGER -> USER)을 STOMP 인가에도 적용하기 위해 주입
  private final RoleHierarchy roleHierarchy;
  // 인바운드 처리 예외의 실제 원인을 로깅하는 STOMP 에러 핸들러
  private final StompErrorHandler stompErrorHandler;

  // 메시지 브로커 구성
  @Override
  public void configureMessageBroker(MessageBrokerRegistry config) {
    // 클라이언트가 구독할 때 사용하는 prefix
    config.enableSimpleBroker("/sub");
    // 클라이언트가 발행할 때 사용하는 prefix (@MessageMapping으로 라우팅됨)
    config.setApplicationDestinationPrefixes("/pub");
  }

  // 클라이언트가 WebSocket 연결을 맺는 STOMP 엔드포인트를 등록
  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    // 예외의 실제 원인을 로깅하도록 커스텀 에러 핸들러 등록
    registry.setErrorHandler(stompErrorHandler);
    // /ws 경로를 STOMP 핸드셰이크 엔드포인트로 등록
    // 클라이언트는 이 엔드 포인트로 최초 HTTP 업그레이드 요청을 보내 WebSocket 연결을 수립한 뒤 STOMP 프로토콜로 통신 시작
    // .withSockJS(): WebSocket을 지원하지 않는 환경을 위한 SockJS 폴백 방식을 활성화
    registry.addEndpoint("/ws").withSockJS();
  }

  // 클라이언트 -> 서버 방향의 인바운드 채널에 인터셉터를 등록
  @Override
  public void configureClientInboundChannel(ChannelRegistration registration) {
    registration.interceptors(
        stompAuthChannelInterceptor,
        // Spring Security 제공 SecurityContext 전파 인터셉터
        // 매 STOMP 프레임마다 메시지에 담긴 Principal을 꺼내 SecurityContextHolder에 Authentication으로 설정
        new SecurityContextChannelInterceptor(),
        // 전파된 인증 정보로 메시지 권한 검사 (인증 -> 전파 -> 인가 순서 보장 필수)
        authorizationChannelInterceptor());
  }

  // STOMP 메시지 인가 인터셉터
  // SecurityContextChannelInterceptor가 전파해 둔 Authentication을 꺼내,
  // 아래 messageAuthorizationManager()의 규칙에 따라 메시지별 접근 허용/거부를 판단한다
  private AuthorizationChannelInterceptor authorizationChannelInterceptor() {
    // 인가 규칙(messageAuthorizationManager)을 주입해 인터셉터를 만든다
    return new AuthorizationChannelInterceptor(messageAuthorizationManager());
  }

  // STOMP 메시지 인가 규칙
  //   발행(/pub), 구독(/sub)은 USER 권한 필요. HTTP 시큐리티와 동일한 역할 계층을 적용해
  //   ADMIN, CHANNEL_MANAGER도 USER 권한을 만족하도록 함
  //   (권한 계층 미적용 되어서 ADMIN 계정이 Access Denied 되는 문제 수정함)
  private AuthorizationManager<Message<?>> messageAuthorizationManager() {
    AuthorityAuthorizationManager<MessageAuthorizationContext<?>> hasUser =
        AuthorityAuthorizationManager.hasRole(UserRole.USER.name());
    hasUser.setRoleHierarchy(roleHierarchy); // 역할 계층 적용

    // 목적지/메시지 타입별로 서로 다른 인가 규칙을 위임 적용하는 매니저를 빌드
    return MessageMatcherDelegatingAuthorizationManager.builder()
        .simpTypeMatchers(
            SimpMessageType.CONNECT, // STOMP 연결 수립 프레임
            SimpMessageType.DISCONNECT, // 연결 종료 프레임
            SimpMessageType.UNSUBSCRIBE, // 구독 취소 프레임
            SimpMessageType.HEARTBEAT // 연결 유지용 하트비트 프레임
            // -> 해당 제어성 프레임은 인가 검사 없이 모두 허용
            // (실제 사용자 인증은 CONNECT 시점에 StompAuthChannelInterceptor가 이미 처리)
        ).permitAll() // 위 4가지 타입은 무조건 허용
        // 클라이언트가 /pub/**로 발행하는 메시지는 USER 권한이 있어야 허용
        .simpDestMatchers("/pub/**").access(hasUser)
        // 클라이언트가 /sub/**를 구독하는 SUBSCRIBE 프레임은 USER 권한이 있어야 하용
        .simpSubscribeDestMatchers("/sub/**").access(hasUser)
        .anyMessage().denyAll() // 나머지 거부
        .build();
  }
  // https://docs.spring.io/spring-framework/reference/web/websocket/stomp.html
  // https://docs.spring.io/spring-security/reference/servlet/integrations/websocket.html
}
