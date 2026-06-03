package com.sprint.mission.discodeit.security.session.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.csrf.CsrfTokenRequestHandler;
import org.springframework.security.web.csrf.XorCsrfTokenRequestAttributeHandler;
import org.springframework.util.StringUtils;

import java.util.function.Supplier;

/*
    SpaCsrfTokenRequestHandler
    --------------------------
    기본 CSRF 검문소 대체
 */
public class SpaCsrfTokenRequestHandler implements CsrfTokenRequestHandler {

    private final CsrfTokenRequestHandler plain = new CsrfTokenRequestAttributeHandler();           // 기본 토큰 처리
    private final CsrfTokenRequestHandler xor = new XorCsrfTokenRequestAttributeHandler();          // 마스크(XOR) 토큰 처리

    // 응답: 서버가 클리이언트에게 토큰 발급
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, Supplier<CsrfToken> csrfToken) {
        // 마스크(XOR) 적용
        this.xor.handle(request, response, csrfToken);
        csrfToken.get();
    }

    // 요청: 클라이언트의 토큰 검증
    @Override
    public String resolveCsrfTokenValue(HttpServletRequest request, CsrfToken csrfToken) {
        // 요청 헤더(X-XSRF-TOKEN)로부터 토큰 분리
        String headerValue = request.getHeader(csrfToken.getHeaderName());

        return (StringUtils.hasText(headerValue)
                ? this.plain                                                // 헤더에 토큰이 있을 경우
                : this.xor).resolveCsrfTokenValue(request, csrfToken);      // 헤더에 토큰이 없을 경우
    }
}
