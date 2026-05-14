package com.sprint.mission.discodeit.config.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.csrf.CsrfTokenRequestHandler;
import org.springframework.security.web.csrf.XorCsrfTokenRequestAttributeHandler;
import org.springframework.util.StringUtils;

import java.util.function.Supplier;

//정리
//SPA(CSR) + SSR(form submit)
//둘 다 호환되게 처리하는 "혼합형 CSRF 핸들러"

//이 핸들러는 요청에서 CSRF 토큰을 어떻게 읽을지와,
//응답 흐름에서 CSRF 토큰을 어떻게 노출할지를 결정/연결하는 역할
//실제 Set-Cookie를 직접 만든느 주체는 CsrfTokenRepository

/**
 * CSR / SPA 환경에서 사용하기 위한 CSRF Token Request Handler.
 *
 * Spring Security 6부터 기본 CSRF 토큰 처리 핸들러는
 * XorCsrfTokenRequestAttributeHandler이다. -> SSR form hidden input용 기본 구현체
 *
 * XorCsrfTokenRequestAttributeHandler는 BREACH 공격 방어를 위해
 * 응답에 노출되는 CSRF 토큰 값을 매 요청마다 XOR 방식으로 인코딩한다.
 *
 * 하지만 SPA에서는 보통 서버가 CSRF 토큰을 쿠키로 내려주고,
 * 프론트엔드 JavaScript가 그 쿠키 값을 읽어서 요청 헤더에 담아 보낸다.
 *
 * 이때 프론트가 읽는 쿠키 값은 "원본 CSRF 토큰"이므로,
 * 요청 헤더로 들어온 토큰은 XOR 디코딩 대상이 아니라 plain token으로 처리해야 한다.
 */
// CCsrfTokenRequestHandler: 요청/응답 과정에서 CSRF 토큰을 어떻게 노출하고, 요청에서 어떻게 읽어올지 ..
public class SpaCsrfTokenRequestHandler implements CsrfTokenRequestHandler {

    /**
     * plain 핸들러.
     *
     * CsrfTokenRequestAttributeHandler는 XOR 인코딩/디코딩을 하지 않고
     * 요청에서 전달된 CSRF 토큰 값을 그대로 읽는다.
     *
     * SPA 프론트엔드가 쿠키에서 읽은 원본 CSRF 토큰을
     * X-XSRF-TOKEN 같은 헤더에 담아 보낼 때 사용한다.
     */
    private final CsrfTokenRequestHandler plain = new CsrfTokenRequestAttributeHandler();

    /**
     * xor 핸들러.
     *
     * XorCsrfTokenRequestAttributeHandler는 Spring Security의 기본 구현체이다.
     * BREACH 공격 방어를 위해 CSRF 토큰을 XOR 처리하여
     * 매 요청마다 응답에 노출되는 토큰 값을 다르게 만든다.
     *
     * SSR form hidden input 방식처럼 서버가 HTML에 CSRF 토큰을 렌더링하는 경우
     * 이 핸들러를 사용하는 것이 적합하다.
     */
    private final CsrfTokenRequestHandler xor = new XorCsrfTokenRequestAttributeHandler();

    //(1)클라이언트가 서버에 CSRF 토큰 발급 요청을 보낸다.
    //GET /api/csrf

    //handle은 항상 xor을 써서 "응답 body/HTML에 노출될 수 있는 CSRF 토큰"은 XOR방식으로 준비한다.

    //그리고 SPA를 위해 csrfToKEN.GET()을 호출해서
    //CookieCsrfTokenRepository가 원본 CSRF 토큰을 Set-Cookie로 내려보내게 만든다.
    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            Supplier<CsrfToken> csrfToken
    ) {

        //request attribute에 XOR 토큰 준비
        //SSR/HTML 렌더링용 대비
        //SPA에서는 보통 안 씀
        this.xor.handle(request, response, csrfToken);

        //raw token 로딩
        //CookieCsrfTokenRepository가 Set-Cookie로 원본 토큰 내려주게 함
        //SPA에서 실제로 필요한 부분
        //토큰이 없으면 생성하고, 있으면 기존 토큰을 가져오는 역할
        csrfToken.get();
    }

    /**
     * 클라이언트 요청에서 CSRF 토큰 값을 꺼내는 메서드.
     *
     * 핵심:
     * - 헤더에 CSRF 토큰이 있으면 SPA 요청으로 보고 plain 방식으로 읽는다.
     * - 헤더가 없고 request parameter로 들어온 경우는 SSR form 요청으로 보고 xor 방식으로 읽는다.
     */
    @Override
    public String resolveCsrfTokenValue(
            HttpServletRequest request,
            CsrfToken csrfToken
    ) {
        /*
         * csrfToken.getHeaderName()은 CSRF 토큰을 담아야 하는 헤더 이름을 반환한다.
         *
         * CookieCsrfTokenRepository.withHttpOnlyFalse()를 사용하는 경우
         * 일반적으로 헤더 이름은 "X-XSRF-TOKEN"이다.
         *
         * 프론트엔드는 쿠키의 XSRF-TOKEN 값을 읽어서
         * 요청 헤더 X-XSRF-TOKEN에 담아 서버로 보낸다.
         */
        String headerValue = request.getHeader(csrfToken.getHeaderName());

        /*
         * 요청 헤더에 CSRF 토큰 값이 존재하면 SPA 요청으로 판단한다.
         *
         * SPA에서는 JavaScript가 쿠키에서 읽은 "원본 CSRF 토큰"을
         * 헤더에 그대로 넣어서 보내기 때문에 plain 핸들러로 처리해야 한다.
         *
         * 반대로 헤더가 없다면,
         * SSR form에서 hidden input인 _csrf request parameter로 전달된 요청일 수 있다.
         * 이 경우에는 Spring Security 기본 방식인 xor 핸들러로 처리한다.
         */
        return StringUtils.hasText(headerValue)
                ? this.plain.resolveCsrfTokenValue(request, csrfToken)
                : this.xor.resolveCsrfTokenValue(request, csrfToken);
    }
}
