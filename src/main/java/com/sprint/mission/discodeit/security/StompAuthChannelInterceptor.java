package com.sprint.mission.discodeit.security;

import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.security.jwt.JwtRegistry;
import com.sprint.mission.discodeit.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

// STOMP 인바운드 채널에서 JWT 기반 인증을 처리하는 채널 인터셉터
@Slf4j
@Component
@RequiredArgsConstructor
public class StompAuthChannelInterceptor implements ChannelInterceptor {

  private static final String BEARER_PREFIX = "Bearer ";

  private final JwtTokenProvider jwtTokenProvider;
  private final JwtRegistry jwtRegistry;
  private final UserDetailsService userDetailsService;

  // STOMP 프레임이 채널을 통해 전달되기 직전에 실행되는 인터셉터 메서드
  // CONNECT 프레임에서만 JWT 인증을 수행하고, 나머지 프레임은 그대로 통과시킴
  // message: 클라이언트가 보낸 STOMP 프레임 하나를 Spring이 감싼 객체
  @Override
  public Message<?> preSend(Message<?> message, MessageChannel channel) {
    // 주의: StompHeaderAccessor.wrap(message)는 헤더를 복사한 새 accessor를 만들기 때문에
    // 거기에 setUser()를 해도 원본 message에 반영되지 않아 세션에 Principal이 붙지 않는다.
    // 원본 message에 연결된 가변 accessor를 가져와야 setUser()가 실제로 반영된다. (Spring 공식 패턴)
    // https://docs.spring.io/spring-framework/reference/web/websocket/stomp/authentication-token-based.html
    StompHeaderAccessor accessor =
        MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
    if (accessor == null) {
      return message;
    }

    // CONNECT 프레임일 때만 JWT 인증 수행
    if (StompCommand.CONNECT.equals(accessor.getCommand())) {
      try {
        String token = resolveToken(accessor); // Authorization 헤더에서 JWT 추출
        // 토큰 유효성 검증 후 인증 객체 생성
        UsernamePasswordAuthenticationToken authentication = authenticate(token);
        // 인증 객체를 STOMP 세션의 Principal로 등록
        accessor.setUser(authentication);
        log.debug("STOMP CONNECT 인증 성공: user={}", authentication.getName());
      } catch (RuntimeException e) {
        // 실패 시 그냥 throw하면 사유가 어디에도 안 남으므로 여기서 명시적으로 로깅 후 재던짐
        log.warn("STOMP CONNECT 인증 실패: sessionId={}, type={}, reason={}",
            accessor.getSessionId(), e.getClass().getSimpleName(), e.getMessage());
        throw e;
      }
    }

    return message;
  }

  // Authorization 헤더에서 Bearer 토큰 추출
  private String resolveToken(StompHeaderAccessor accessor) {
    String authHeader = accessor.getFirstNativeHeader("Authorization");
    if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
      throw new MessagingException(ErrorCode.INVALID_TOKEN.getMessage());
    }
    return authHeader.substring(BEARER_PREFIX.length());
  }

  // 서명/만료 검증 + Registry 활성 여부 확인 후 인증 객체 생성
  private UsernamePasswordAuthenticationToken authenticate(String token) {
    // JWT 서명 및 만료 시간 검증
    if (!jwtTokenProvider.validateToken(token)) {
      throw new BadCredentialsException(ErrorCode.INVALID_TOKEN.getMessage());
    }

    // 레지스트리에서 토큰 활성 여부 확인.
    if (!jwtRegistry.hasActiveJwtInformationByAccessToken(token)) {
      throw new BadCredentialsException(ErrorCode.INVALID_TOKEN.getMessage());
    }

    // 토큰에서 username 추출 후 UserDetails 로드
    String username = jwtTokenProvider.extractUsername(token);
    UserDetails userDetails = userDetailsService.loadUserByUsername(username);

    // 인증 완료 상태의 Authentication 객체 생성
    return new UsernamePasswordAuthenticationToken(
        userDetails,
        null,
        userDetails.getAuthorities());
  }
}
