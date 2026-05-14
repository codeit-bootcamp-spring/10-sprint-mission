package com.sprint.mission.discodeit.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.function.Supplier;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.csrf.CsrfTokenRequestHandler;
import org.springframework.security.web.csrf.XorCsrfTokenRequestAttributeHandler;

public class SpaCsrfTokenRequestHandler implements CsrfTokenRequestHandler {
    private final CsrfTokenRequestHandler plain = new CsrfTokenRequestAttributeHandler();
    private final CsrfTokenRequestHandler xor = new XorCsrfTokenRequestAttributeHandler();

    @Override
    public void handle(
        HttpServletRequest request,
        HttpServletResponse response,
        Supplier<CsrfToken> csrfToken
    ) {
        // 응답에 CSRF 토큰이 렌더링될 가능성에 대비해 기본적으로 XOR 핸들러를 사용합니다.
        this.xor.handle(request, response, csrfToken);

        // 지연 로딩된 CSRF 토큰을 강제로 생성합니다.
        // CookieCsrfTokenRepository를 사용할 때 이 호출을 통해 XSRF-TOKEN 쿠키가 응답에 실릴 수 있습니다.
        csrfToken.get();
    }

}
