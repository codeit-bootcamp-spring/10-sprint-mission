package com.sprint.mission.discodeit.auth.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.function.Supplier;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.csrf.CsrfTokenRequestHandler;
import org.springframework.security.web.csrf.XorCsrfTokenRequestAttributeHandler;
import org.springframework.util.StringUtils;

/**
 * CSR/SPA 환경을 위한 CSRF 토큰 요청 핸들러.
 *
 * Spring Security 6의 기본 CSRF 요청 핸들러는 XorCsrfTokenRequestAttributeHandler이다.
 * 이 방식은 서버가 HTML을 렌더링하면서 CSRF 토큰을 화면에 포함하는 SSR 방식에 적합하다.
 *
 * 하지만 CSR/SPA에서는 클라이언트 JavaScript가 XSRF-TOKEN 쿠키 값을 읽고,
 * 그 값을 X-XSRF-TOKEN 요청 헤더에 담아 보낸다.
 *
 * 따라서 요청 헤더에 CSRF 토큰이 있는 경우에는 plain token 방식으로 처리하고,
 * 그 외의 경우에는 기존 XOR 방식으로 처리한다.
 */
public final class SpaCsrfTokenRequestHandler implements CsrfTokenRequestHandler {

  /*
   * plain handler
   *
   * CSR/SPA 요청에서 사용한다.
   * 클라이언트가 XSRF-TOKEN 쿠키 값을 읽어서
   * X-XSRF-TOKEN 헤더에 그대로 담아 보내는 경우에 적합하다.
   */
  private final CsrfTokenRequestHandler plain = new CsrfTokenRequestAttributeHandler();

  /*
   * xor handler
   *
   * SSR form 렌더링 등에서 BREACH 공격 방어를 위해 사용한다.
   */
  private final CsrfTokenRequestHandler xor = new XorCsrfTokenRequestAttributeHandler();

  @Override
  public void handle(
      HttpServletRequest request,
      HttpServletResponse response,
      Supplier<CsrfToken> csrfToken
  ) {
    /*
     * CSR/SPA 환경에서는 클라이언트가 쿠키를 직접 읽어 헤더에 담아야 하므로,
     * XOR 처리가 되지 않은 plain token을 사용해야 한다.
     */
    this.plain.handle(request, response, csrfToken);

    /*
     * Spring Security 6에서는 CSRF 토큰이 지연 로딩될 수 있다.
     *
     * CSR/SPA에서는 클라이언트가 먼저 GET /api/auth/csrf-token을 호출해서
     * XSRF-TOKEN 쿠키를 받아야 한다.
     *
     * csrfToken.get()을 호출해야 실제 토큰이 생성되고,
     * CookieCsrfTokenRepository가 응답에 Set-Cookie를 내려줄 수 있다.
     */
    csrfToken.get();
  }

  @Override
  public String resolveCsrfTokenValue(HttpServletRequest request, CsrfToken csrfToken) {
    /*
     * CookieCsrfTokenRepository의 기본 헤더 이름은 X-XSRF-TOKEN이다.
     */
    String headerValue = request.getHeader(csrfToken.getHeaderName());

    /*
     * X-XSRF-TOKEN 헤더가 있다면 CSR/SPA 요청으로 본다.
     * 이 경우 클라이언트가 쿠키에서 읽은 plain token을 보낸 것이므로 plain handler 사용.
     *
     * 헤더가 없다면 SSR form 요청일 수 있다.
     * 이 경우 _csrf 파라미터 기반의 XOR 처리 방식을 사용.
     */
    return StringUtils.hasText(headerValue)
        ? this.plain.resolveCsrfTokenValue(request, csrfToken)
        : this.xor.resolveCsrfTokenValue(request, csrfToken);
  }
}