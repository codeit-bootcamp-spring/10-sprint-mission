package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.AuthApi;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController implements AuthApi {

  private final AuthService authService;

  @PostMapping(path = "login")
  public ResponseEntity<UserDto> login(@RequestBody @Valid LoginRequest loginRequest) {
    log.info("로그인 요청: username={}", loginRequest.username());
    UserDto user = authService.login(loginRequest);
    log.debug("로그인 응답: {}", user);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(user);
  }

  //CsrfToken 파라미터를 메서드 인자로 선언하면,
  //HandlerMethodArgumentResolver를 통해 자동으로 주입된다.

  /**
   1. 클라이언트가 토큰 발급 API 호출--
   GET /api/auth/csrf-token HTTP/1.1
   Host: localhost:8080

   GET 요청은 CSRF 검증대상이 아니다.

   2. SpaCsrfTokenRequestHandler에서 csrfToken.get()의 호출때문에
      CSRF 토큰이 실제로 생성된다.

   token = "8f4d9b2e-7c13-45a8-b2fd-91f305d4e1aa"

   3. CookieCsrfTokenRepository가 응답 쿠키에 토큰 저장한다.
   Set-Cookie: XSRF-TOKEN=8f4d9b2e-7c13-45a8-b2fd-91f305d4e1aa; Path=/

   4. 브라우저는 JS에서 쿠키를 읽을 수 있다.
   document.cookie
   // "XSRF-TOKEN=8f4d9b2e-7c13-45a8-b2fd-91f305d4e1aa"

   5. 컨트롤러 메서드에 CsrfToken이 자동 주입됨.
   HandlerMethodArgumentResolver가 request attribute(Http 요청 처리 임시 공간)에 있는 CSRF 토큰을 찾아서 넣어줌.

   **/
  @GetMapping("/csrf-token")
  public ResponseEntity<Void> getCsrfToken(CsrfToken csrfToken) {
    String tokenValue = csrfToken.getToken();
    log.debug("CSRF 토큰 요청: {}", tokenValue);
    //응답 203 Void
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }
}
