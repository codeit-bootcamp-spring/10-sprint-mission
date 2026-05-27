package com.sprint.mission.discodeit.config.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.csrf.CsrfTokenRequestHandler;
import org.springframework.security.web.csrf.XorCsrfTokenRequestAttributeHandler;
import org.springframework.util.StringUtils;

import java.util.function.Supplier;

// SPA 환경에서 CSRF 토큰을 처리하기 위한 커스텀 CsrfTokenRequestHandler
public class SpaCsrfTokenRequestHandler implements CsrfTokenRequestHandler {

    // 클라이언트가 쿠키에서 읽은 CSRF 토큰을 헤더로 보낼 때 사용
    private final CsrfTokenRequestHandler csrf = new CsrfTokenRequestAttributeHandler();
    // 서바가 화면이나 응답에 노출할 CSRF 토큰을 마스킹하거나, 마스킹된 CSRF 토큰을 원래의 값으로 복원할 때 사용
    private final CsrfTokenRequestHandler xorCsrf = new XorCsrfTokenRequestAttributeHandler();

    // 클라이언트 요청에 포함된 CSRF 토큰 값을 꺼내는 메서드
    @Override
    public String resolveCsrfTokenValue(
            HttpServletRequest request,
            CsrfToken csrfToken
    ) {
        // 요청에 CSRF 토큰 헤더가 포함되어 있는지 확인
        // 기본적으로 `X-XSRF-TOKEN` 같은 헤더를 확인
        String header = request.getHeader(csrfToken.getHeaderName());

        // CSR/SPA 요청인지 판단
        // 헤더가 없으면 xor handler 사용
        CsrfTokenRequestHandler csrfTokenRequestHandler = StringUtils.hasText(header)
                ? this.csrf
                : this.xorCsrf;

        // 선택된 handler를 통해 요청에서 CSRF 토큰을 꺼냄
        return csrfTokenRequestHandler.resolveCsrfTokenValue(request, csrfToken);
    }

    // 서버 내부에서 현재 요청의 CSRF 토큰을 사용할 수 있도록 request attribute에 등록하는 메서드
    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            Supplier<CsrfToken> csrfToken
    ) {
        // request attribute에 CSRF 토큰 노출 시 기본적으로 xor handler 사용
        // 매 요청마다 마스킹되어 BREACH 공격 방어에 도움이 됨
        this.xorCsrf.handle(request, response, csrfToken);

        // 지연 로딩된 토큰을 바로 로드
        csrfToken.get();
    }
}
