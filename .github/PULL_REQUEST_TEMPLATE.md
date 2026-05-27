# Spring Security 환경설정
- [X]  프로젝트에 Spring Security 의존성을 추가하세요.
- [X]  Security 설정 클래스를 생성하세요.

패키지명: com.sprint.mission.discodeit.config
클래스명: SecurityConfig
- [x]  SecurityFilterChain Bean을 선언하세요.
- [ ]  가장 기본적인 SecurityFilterChain을 등록하고, 이때 등록되는 필터 목록을 디버깅해보세요. 필터 목록은 PR에 첨부하세요.

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    return http.build();
}
```

- [X]  개발 환경에서 Spring Security 모듈의 로깅 레벨을 trace로 설정하세요.

각 요청마다 통과하는 필터 목록을 확인할 수 있습니다.

# CSRF 보호 설정

디스코드잇은 CSR 방식이기 때문에 CSRF 토큰을 다음과 같이 처리합니다.

1. 클라이언트에서 페이지가 로드될 때 CSRF 토큰 발급 API를 명시적으로 호출
2. 서버는 CSRF 토큰을 응답 헤더(`Set-Cookie`)를 통해 쿠키에 저장
3. 클라이언트에서 매 요청마다 쿠키에 저장된 CSRF 토큰을 헤더(`X-XSRF-TOKEN`)에 포함
4. 서버는 요청 헤더에 포함된 두 토큰 값(`X-XSRF-TOKEN`, `Cookie`)을 비교해 유효성 검증

---

## 체크리스트

### ✅ `CsrfTokenRepository` 구현체를 `CookieCsrfTokenRepository`로 설정

- 디폴트 구현체는 `HttpSessionCsrfTokenRepository`입니다.
- 클라이언트에서 쿠키에 저장된 CSRF 토큰에 접근해야 하므로 `HttpOnly`는 `false`로 설정합니다.

```java
http
    .csrf(csrf -> csrf
        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
    )
```

---

### ✅ `CsrfTokenRequestHandler` 컴포넌트 대체

- 디폴트 구현체는 `XorCsrfTokenRequestAttributeHandler`입니다.
- [Spring 공식문서](https://docs.spring.io/spring-security/reference/servlet/exploits/csrf.html#csrf-integration-javascript-spa)에서 권장하는 CSR + SPA 환경에 적합한 구현체를 정의합니다.

```java
public class SpaCsrfTokenRequestHandler implements CsrfTokenRequestHandler {
    private final CsrfTokenRequestHandler plain = new CsrfTokenRequestAttributeHandler();
    private final CsrfTokenRequestHandler xor = new XorCsrfTokenRequestAttributeHandler();

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, Supplier<CsrfToken> csrfToken) {
        /*
         * Always use XorCsrfTokenRequestAttributeHandler to provide BREACH protection of
         * the CsrfToken when it is rendered in the response body.
         */
        this.xor.handle(request, response, csrfToken);
        /*
         * Render the token value to a cookie by causing the deferred token to be loaded.
         */
        csrfToken.get();
    }

    @Override
    public String resolveCsrfTokenValue(HttpServletRequest request, CsrfToken csrfToken) {
        String headerValue = request.getHeader(csrfToken.getHeaderName());
        /*
         * If the request contains a request header, use CsrfTokenRequestAttributeHandler
         * to resolve the CsrfToken. This applies when a single-page application includes
         * the header value automatically, which was obtained via a cookie containing the
         * raw CsrfToken.
         *
         * In all other cases (e.g. if the request contains a request parameter), use
         * XorCsrfTokenRequestAttributeHandler to resolve the CsrfToken. This applies
         * when a server-side rendered form includes the _csrf request parameter as a
         * hidden input.
         */
        return (StringUtils.hasText(headerValue) ? this.plain : this.xor).resolveCsrfTokenValue(request, csrfToken);
    }
}
```

```java
http
    .csrf(csrf -> csrf
        ...
        .csrfTokenRequestHandler(new SpaCsrfTokenRequestHandler())
    )
```

---

### ✅ CSRF 토큰 발급 API 구현

**API 스펙**

| 항목 | 내용 |
|------|------|
| 엔드포인트 | `GET /api/auth/csrf-token` |
| 요청 | 없음 |
| 응답 | `204 Void` |

```java
@GetMapping("csrf-token")
public ResponseEntity<Void> getCsrfToken(CsrfToken csrfToken) {
    String tokenValue = csrfToken.getToken();
    log.debug("CSRF 토큰 요청: {}", tokenValue);
    ...
}
```

> **참고**
> - `CsrfToken` 파라미터를 메서드 인자로 선언하면, `HandlerMethodArgumentResolver`를 통해 자동으로 주입됩니다. ([공식문서](https://docs.spring.io/spring-security/reference/servlet/integrations/mvc.html#mvc-csrf-resolver))
> - GET 요청에는 CSRF 인증이 이루어지지 않기 때문에 토큰이 초기화되지 않습니다. 따라서 메서드에서 토큰을 명시적으로 호출합니다.

# 회원가입
[X] 회원가입 API 스펙은 유지합니다.
- API 스펙
  - 엔드포인트: POST /api/users
  - 요청: Body UserCreateRequest, MultipartFile
  - 응답: 200 UserDto
- [X] 회원가입 시 비밀번호는 PasswordEncoder를 통해 해시로 저장하세요.
  - PasswordEncoder의 구현체는 BCryptPasswordEncoder를 활용하세요.

# 인증 - 로그인

## formLogin 활성화

- [X] `formLogin` 을 기본값으로 활성화하고, 추가된 필터를 확인해보세요.

```java
http
    .formLogin(Customizer.withDefaults());
```

---

## 인증 컴포넌트 대체

- 인증 흐름은 그대로 유지하면서 필요한 부분만 대체합니다.
- 이번 미션에서는 아래 5가지 컴포넌트를 대체합니다.

### 대체 대상

- `UserDetails`
- `UserDetailsService`
- `PasswordEncoder`
    - 이전에 정의한 `BCryptPasswordEncoder` 사용
- `AuthenticationSuccessHandler`
- `AuthenticationFailureHandler`

- [ ] 각 컴포넌트의 기본 구현체가 무엇인지 디버깅해보세요.

---

## 로그인 처리 URL 변경

- [X] 로그인을 처리할 URL을 `/api/auth/login` 으로 설정하세요.

```java
http
    .formLogin(login -> login
        .loginProcessingUrl("/api/auth/login")
    );
```

---

# UserDetailsService 대체

- [X] `UserDetailsService` 컴포넌트를 대체하세요.

- 디폴트 구현체는 `InMemoryUserDetailsManager` 입니다.

- `DiscodeitUserDetailsService` 를 정의하세요.

```java
@Service
@RequiredArgsConstructor
public class DiscodeitUserDetailsService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        ...
    }
}
```

### 요구사항

- Discodeit DB에서 자체 관리하는 사용자 정보로 `UserDetails` 객체를 생성합니다.
- 구현체를 Bean으로 등록하면 자동으로 대체됩니다.

---

# UserDetails 대체

- [X] `UserDetails` 컴포넌트를 대체하세요.

- 디폴트 구현체는 다음 클래스입니다.

```text
org.springframework.security.core.userdetails.User
```

- `DiscodeitUserDetails` 를 정의하세요.

```java
@Getter
@RequiredArgsConstructor
public class DiscodeitUserDetails implements UserDetails {

    private final UserDto userDto;
    private final String password;

    ...
}
```

### 요구사항

- 인증 정보(Principal)에 담을 수 있는 정보를 자유롭게 확장할 수 있습니다.
- `UserDto` 와 비밀번호 정보를 저장하세요.
- 앞서 정의한 `DiscodeitUserDetailsService` 에서 `DiscodeitUserDetails` 를 생성 후 반환하세요.

---

# AuthenticationSuccessHandler 대체

- [X] `AuthenticationSuccessHandler` 컴포넌트를 대체하세요.

- 디폴트 구현체는 다음 클래스입니다.

```text
SavedRequestAwareAuthenticationSuccessHandler
```

- `LoginSuccessHandler` 를 정의하고 대체하세요.

```java
@Component
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {

        ...
    }
}
```

### 요구사항

- 인증 성공 시 `200 OK` 와 함께 `UserDto` 로 응답합니다.
- 설정에 추가하세요.

```java
http
    .formLogin(login -> login
        ...
        .successHandler(loginSuccessHandler)
    );
```

---

# AuthenticationFailureHandler 대체

- [X] `AuthenticationFailureHandler` 컴포넌트를 대체하세요.

- 디폴트 구현체는 다음 클래스입니다.

```text
SimpleUrlAuthenticationFailureHandler
```

- `LoginFailureHandler` 를 정의하고 대체하세요.

```java
@Component
public class LoginFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception
    ) throws IOException, ServletException {

        ...
    }
}
```

### 요구사항

- 인증 실패 시 `401 Unauthorized` 와 함께 `ErrorResponse` 로 응답합니다.
- 설정에 추가하세요.

```java
http
    .formLogin(login -> login
        ...
        .failureHandler(loginFailureHandler)
    );
```

---

# 기존 로그인 코드 제거

- [X] 이제 로그인 처리는 `SecurityFilterChain` 에서 모두 처리되므로 기존 로그인 관련 코드를 제거하세요.

### 제거 대상

- `AuthApi.login`
- `AuthController.login`
- `AuthService.login`
- `LoginRequest`