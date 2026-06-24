package com.sprint.mission.discodeit.security;

import java.security.Principal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.StompSubProtocolErrorHandler;

/*
STOMP 인바운드 처리 중 발생한 예외의 실제 원인(cause)을 서버 로그에 남기는 핸들러.
기본 동작은 MessageDeliveryException 래퍼만 클라이언트로 보내고 서버엔 사유가 안 남기 때문에,
여기서 cause를 풀어 로깅하여 CONNECT 인증 실패 / 메시지 인가 실패 / 핸들러 예외를 식별 가능하게 한다.

STOMP(WebSocket 위에서 동작하는 메시징 프로토콜)에서 인바운드 메시지를 처리하다 예외가 나면,
스프링은 예외를 클라이언트에게 ERROR 프레임으로 보낸다.
그런데 이 예외는 핸들러에 도달하기 전에 스프링 메시징 인프라에서 MessageDeliveryException 같은 래퍼로 한 번 감싸지고
기본 핸들러(StompSubProtocolErrorHandler)는 이를 별도로 로깅하지 않는다.
그래서 서버 로그만 봐서는 실패 원인을 파악하기 어려워서, 기본 핸들러를 확장해 전달받은 예외의 진짜 원인을 풀어 WARN 로그로 남기고,
클라이언트로 가는 ERROR 프레임 생성은 그대로 부모(기본 핸들러)에 위임한다.
*/
@Slf4j
@Component
public class StompErrorHandler extends StompSubProtocolErrorHandler {

  // Message<byte[]>: 클라이언트에게 다시 내려보낼 STOMP ERROR 프레임 (STOMP 프레임은 바이트 페이로드)
  @Override
  public Message<byte[]> handleClientMessageProcessingError(
      // 예외를 유발한 원본 클라이언트 메시지, 발생한 예외
      @Nullable Message<byte[]> clientMessage, Throwable ex) {
    // 감싸진 실제 원인을 추출해 로깅
    Throwable cause = (ex.getCause() != null) ? ex.getCause() : ex;
    log.warn("STOMP 메시지 처리 오류: type={}, message={}",
        cause.getClass().getSimpleName(), cause.getMessage(), cause);
    // 거부 프레임의 Principal/권한을 까서 인증 전파 실패(anonymous) 와 인가 규칙 거부를 구분 가능하게 함.
    // CONNECT 인증 실패면 user=null, 전파 실패면 user=anonymous(authorities=[]) 로 찍힌다.
    if (log.isDebugEnabled() && clientMessage != null) {
      StompHeaderAccessor accessor =
          MessageHeaderAccessor.getAccessor(clientMessage, StompHeaderAccessor.class);
      Principal user = (accessor != null) ? accessor.getUser() : null;
      Object authorities = (user instanceof Authentication auth) ? auth.getAuthorities() : null;
      log.debug(
          "STOMP 거부 프레임 상세: command={}, destination={}, sessionId={}, user={}, authorities={}",
          (accessor != null) ? accessor.getCommand() : null,
          (accessor != null) ? accessor.getDestination() : null,
          (accessor != null) ? accessor.getSessionId() : null,
          user, authorities);
    }
    // ERROR 프레임 생성은 기본 동작에 위임 (클라이언트로는 기존과 동일한 ERROR 프레임 전송)
    // 로깅만 추가하고 클라이언트가 받는 응답은 기존과 동일
    return super.handleClientMessageProcessingError(clientMessage, ex);
  }
}
