package com.sprint.mission.discodeit.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.function.Supplier;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.csrf.CsrfTokenRequestHandler;
import org.springframework.security.web.csrf.XorCsrfTokenRequestAttributeHandler;
import org.springframework.util.StringUtils;

public class SpaCsrfTokenRequestHandler implements CsrfTokenRequestHandler {

  private final CsrfTokenRequestHandler plain = new CsrfTokenRequestAttributeHandler();
  private final CsrfTokenRequestHandler xor = new XorCsrfTokenRequestAttributeHandler();

  @Override
  public void handle(
      HttpServletRequest request,
      HttpServletResponse response,
      Supplier<CsrfToken> csrfToken
  ) {
    // 응답 body 등에 csrf 토큰이 노출될 때 breach보호 적용 -> xor방식으로 마스킹
    this.xor.handle(request, response, csrfToken);

    // 지연 로딩된 csrf토큰을 강제로 생성해서 쿠키에 내려가게 함
    csrfToken.get();
  }

  @Override
  public String resolveCsrfTokenValue(
      HttpServletRequest request,
      CsrfToken csrfToken
  ) {

    String headerValue = request.getHeader(csrfToken.getHeaderName());

    // SPA 요청처럼 헤더에 토큰이 있으면 raw token 방식으로 검증
    // 서버 렌더링 form 파라미터 방식이면 xor 방식으로 검증
    CsrfTokenRequestHandler handler = StringUtils.hasText(headerValue)
        ? this.plain
        : this.xor;

    return handler.resolveCsrfTokenValue(request, csrfToken);
  }
}
