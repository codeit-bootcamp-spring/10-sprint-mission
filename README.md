# [SB] 스프린트 미션 9

## 🏔️ 프로젝트 마일스톤

- Sprint Security 환경 설정
- 세션 기반 인증 / 인가

### 주요 변경 사항

**1. 프로젝트 버전이 변경되었습니다.** `v2.0-M9`

- 세부 사항
    ```gradle
    # build.gradle
    
    ...
    version = '1.2-M8'
    version = '2.0-M9'
    ...
    ```
    - `2.0`: api-doc 버전을 따릅니다.
    - `M9`: 미션 9를 의미합니다.

2. 읽기전용 트랜잭션 어노테이션이 추가되었습니다.

- 세부 사항
    - `BasicUserService.find`, `findAll`
    - `BasicUserStatusService.find`, `findAll`
    - `BasicReadStatusService.find`, `findAllByUserId`

3. 프론트엔드가 변경되었습니다.

- `v2.0.7`
    - 채널 수정/삭제 기능이 추가되었습니다.
        - `CHANNEL_MANAGER` 또는 `ADMIN` 권한이 있는 사용자의 경우 채널에 포인터를 올리면 ··· 버튼이 나타납니다.
    - 메시지 수정/삭제 기능이 추가되었습니다.
        - 본인이 작성한 메시지에 포인터를 올리면 ··· 버튼이 나타납니다.
    - 권한처리 기능이 추가되었습니다.
    - UserStatus를 반복적으로 업데이트하는 기능이 삭제되었습니다. (요구사항에서 세션으로 대체할 예정입니다.)

### 유의 사항

> 디스코드잇의 프론트엔드는 서버에서 HTML을 모두 생성하는 SSR(Server Side Rendering) 방식이 아니라, 클라이언트(브라우저)에서 Javascript를 활용해
> HTML을 생성하는 CSR(Client Side Rendering) 방식입니다.
>
> Spring Security는 SSR 환경을 가정한 기본 설정이 많습니다. 따라서 본 미션을 수행할 때 기본 설정을 사용하지 않고 커스터마이징 해야하는 설정이 다소 있는 점을
> 유의하기 바랍니다.
>
> 단, Spring Security의 기본 인증 플로우는 최대한 유지하도록 노력합니다.

## 📝 요구사항

### ✏️ 기본 요구사항

### 01. Spring Security 환경설정

- [x] 프로젝트에 Spring Security 의존성을 추가하세요.
- [x] Security 설정 클래스를 생성하세요.
    - 패키지명:`com.sprint.mission.discodeit.config`
    - 클래스명:`SecurityConfig`
- [x] `SecurityFilterChain` Bean을 선언하세요.
    - [x] 가장 기본적인 `SecurityFilterChain`을 등록하고, 이때 등록되는 필터 목록을 디버깅해보세요. 필터 목록은 PR에 첨부하세요.
    ```java
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
       return http.build();
    }
    ```

https://docs.spring.io/spring-security/reference/servlet/architecture.html

```Markdown
Invoking DisableEncodeUrlFilter (1/10)
Invoking WebAsyncManagerIntegrationFilter (2/10)
Invoking SecurityContextHolderFilter (3/10)
Invoking HeaderWriterFilter (4/10)
Invoking CsrfFilter (5/10)
Invoking LogoutFilter (6/10)
Invoking RequestCacheAwareFilter (7/10)
Invoking SecurityContextHolderAwareRequestFilter (8/10)
Invoking AnonymousAuthenticationFilter (9/10)
Invoking ExceptionTranslationFilter (10/10)

...

26-05-14 11:41:57.033 [http-nio-8080-exec-7] DEBUG o.s.security.web.FilterChainProxy    [ | | ] - Securing POST /api/users
26-05-14 11:41:57.033 [http-nio-8080-exec-7] TRACE o.s.security.web.FilterChainProxy    [ | | ] - Invoking DisableEncodeUrlFilter (1/10)
26-05-14 11:41:57.033 [http-nio-8080-exec-7] TRACE o.s.security.web.FilterChainProxy    [ | | ] - Invoking WebAsyncManagerIntegrationFilter (2/10)
26-05-14 11:41:57.033 [http-nio-8080-exec-7] TRACE o.s.security.web.FilterChainProxy    [ | | ] - Invoking SecurityContextHolderFilter (3/10)
26-05-14 11:41:57.033 [http-nio-8080-exec-7] TRACE o.s.security.web.FilterChainProxy    [ | | ] - Invoking HeaderWriterFilter (4/10)
26-05-14 11:41:57.033 [http-nio-8080-exec-7] TRACE o.s.security.web.FilterChainProxy    [ | | ] - Invoking CsrfFilter (5/10)
26-05-14 11:41:57.069 [http-nio-8080-exec-7] DEBUG o.s.security.web.csrf.CsrfFilter     [ | | ] - Invalid CSRF token found for http://localhost:8080/api/users
26-05-14 11:41:57.069 [http-nio-8080-exec-7] DEBUG o.s.s.w.a.AccessDeniedHandlerImpl    [ | | ] - Responding with 403 status code
26-05-14 11:41:57.069 [http-nio-8080-exec-7] TRACE o.s.s.w.h.writers.HstsHeaderWriter   [ | | ] - Not injecting HSTS header since it did not match request to [Is Secure]
```  

-> CSRF 토큰이 누락되어 요청이 거부되는 것을 확인할 수 있다.

- [x] 개발 환경에서 Spring Security 모듈의 로깅 레벨을 `trace`로 설정하세요.
    - 각 요청마다 통과하는 필터 목록을 확인할 수 있습니다.

### 02. CSRF 보호 설정하기

![66i8irpxz-image.png](https://bakey-api.codeit.kr/api/files/resource?root=static&seqId=14403&version=1&directory=/66i8irpxz-image.png&name=66i8irpxz-image.png)

> 디스코드잇은 CSR 방식이기 때문에 CSRF 토큰은 다음과 같이 처리합니다.
> 1. 클라이언트에서 페이지가 로드될 때 CSRF 토큰 발급 API를 명시적으로 호출
> 2. 서버는 CSRF 토큰을 응답 헤더(`Set-Cookie`)를 통해 쿠키에 저장
> 3. 클라이언트에서 매 요청마다 쿠키에 저장된 CSRF 토큰을 헤더(`X-XSRF-TOKEN`)에 포함
> 4. 서버는 요청 헤더에 포함된 두 토큰 값(`X-XSRF-TOKEN`,`Cookie`)을 비교해 유효성 검증

- [x] `CsrfTokenRepository` 구현체를 `CookieCsrfTokenRepository`로 설정하세요.
    - 디폴트 구현체는`HttpSessionCsrfTokenRepository`입니다.
  ```java
  http
      .csrf(csrf -> csrf
        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
  )
  ```
    - 이때 클라이언트에서 쿠키에 저장된 CSRF 토큰에 접근해야 하므로**Http Only**는`false`로 설정합니다.
- [x] `CsrfTokenRequestHandler` 컴포넌트를 대체하세요.
    - 디폴트 구현체는 `XorCsrfTokenRequestAttributeHandler`입니다.
    - [Spring 공식문서](https://docs.spring.io/spring-security/reference/servlet/exploits/csrf.html#csrf-integration-javascript-spa)
      에서 권장하는 CSR+SPA(Single Page Application) 환경에 적합한 구현체를 정의하세요.
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
- [x] CSRF 토큰을 발급하는 API를 구현하세요.
    - API 스펙
        - 엔드포인트:`GET /api/auth/csrf-token`
        - 요청: 없음
        - 응답:`203 Void`
    ```java
    @GetMapping("csrf-token")
    public ResponseEntity<Void> getCsrfToken(CsrfToken csrfToken) {
      String tokenValue = csrfToken.getToken();
      log.debug("CSRF 토큰 요청: {}", tokenValue);    
      ...
    }
    ```
    - `CsrfToken`파라미터를 메서드 인자로 선언하면,`HandlerMethodArgumentResolver`를 통해 자동으로
      주입됩니다. ([공식문서](https://docs.spring.io/spring-security/reference/servlet/integrations/mvc.html#mvc-csrf-resolver))
    - GET 요청에는 CSRF 인증이 이루어지지 않기 때문에 토큰이 초기화되지 않습니다. 따라서 명시적으로 메소드에서 토큰을 호출합니다.

### 03. 회원가입

- [x] 회원가입 API 스펙은 유지합니다.
    - API 스펙
        - 엔드포인트: `POST /api/users`
        - 요청: `Body UserCreateRequest, MultipartFile`
        - 응답: `200 UserDto`
- [x] 회원가입 시 비밀번호는 `PasswordEncoder`를 통해 해시로 저장하세요.
    - `PasswordEncoder`의 구현체는 `BCryptPasswordEncoder`를 활용하세요.

### 04. 인증 - 로그인

- [x] `formLogin` 을 기본값으로 활성화하고, 추가된 필터를 확인해보세요.

  ```java
  http
      .formLogin(Customizer.withDefaults())
  ```

```markdown
[main] DEBUG o.s.s.web.DefaultSecurityFilterChain [ | | ] - Will secure any request with filters:
DisableEncodeUrlFilter, WebAsyncManagerIntegrationFilter, SecurityContextHolderFilter,
HeaderWriterFilter, CsrfFilter, LogoutFilter, UsernamePasswordAuthenticationFilter,
DefaultResourcesFilter, DefaultLoginPageGeneratingFilter, DefaultLogoutPageGeneratingFilter,
RequestCacheAwareFilter, SecurityContextHolderAwareRequestFilter, AnonymousAuthenticationFilter,
ExceptionTranslationFilter
```

- `GET  /login`   -> **DefaultLoginPageGeneratingFilter** - 로그인 페이지 보여줌
- `POST /login`   -> **UsernamePasswordAuthenticationFilter** - 실제 인증 처리
- `GET  /logout`  -> **DefaultLogoutPageGeneratingFilter** - 로그아웃 페이지 보여줌


- Spring Security의 formLogin 인증 흐름은 그대로 유지하면서 필요한 부분만 대체합니다.

  ![c9g464dhi-image.png](https://bakey-api.codeit.kr/api/files/resource?root=static&seqId=14405&version=1&directory=/c9g464dhi-image.png&name=c9g464dhi-image.png)

    - 이번 미션에서는 보라색 음영 처리된 5가지 컴포넌트를 대체합니다.
        1. `UserDetails`
        2. `UserDetailsService`
        3. `PasswordEncoder`: 이전에 정의한`BCryptPasswordEncoder`로 대체됩니다.
        4. `AuthenticationSuccessHandler`
        5. `AuthenticationFailureHandler`
    - 각 컴포넌트의 기본 구현체가 무엇인지 디버깅해보세요.
- [x] 로그인을 처리할 url을 `/api/auth/login`로 설정하세요.

  ```java
  http
      .formLogin(login -> login
          .loginProcessingUrl(...)
      )
  ```

- [x] `UserDetailsService` 컴포넌트를 대체하세요.

    - 디폴트 구현체는 `InMemoryUserDetailsManager`입니다.

    - `DiscodeitUserDetailsService`를 정의하세요.

      ```java
      @Service
      @RequiredArgsConstructor
      public class DiscodeitUserDetailsService implements UserDetailsService {
          @Override
          public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
              ...
          }
      }
      ```

        - 디스코드잇 DB에서 자체 관리하는 사용자 정보로`UserDetails`객체를 생성합니다.
        - 구현체를 Bean으로 등록하면 자동으로 대체됩니다.
- [x] `UserDetails` 컴포넌트를 대체하세요.

    - 디폴트 구현체는 `org.springframework.security.core.userdetails.User`입니다.

    - `DiscodeitUserDetails`를 정의하세요.

      ```java
      @Getter
      @RequiredArgsConstructor
      public class DiscodeitUserDetails implements UserDetails {
          private final UserDto userDto;
          private final String password;

          ...
      }
      ```

        - 인증 정보(`Principal`)에 담을 수 있는 정보를 자유롭게 확장할 수 있습니다.
        - `UserDto`와 비밀번호 정보를 저장하세요.
    - 앞서 정의한 `DiscodeitUserDetailsService`에서 `DiscodeitUserDetails`를 생성 후 반환하세요.

- [x] `AuthenticationSuccessHandler` 컴포넌트를 대체하세요.

    - 디폴트 구현체는 `SavedRequestAwareAuthenticationSuccessHandler`입니다.

    - `LoginSuccessHandler`를 정의하고 대체하세요.

      ```java
      @Component
      public class LoginSuccessHandler implements AuthenticationSuccessHandler {
          ...
          @Override
          public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
              Authentication authentication) throws IOException, ServletException {
              ...
          }
      }
      ```

        - 인증 성공 시`200 UserDto`로 응답합니다.
    - 설정에 추가하세요.

      ```java
      http
          .formLogin(login -> login
              ...
              .successHandler(loginSuccessHandler)
          )
      ```

- [x] `AuthenticiationFailureHandler` 컴포넌트를 대체하세요.

    - 디폴트 구현체는 `SimpleUrlAuthenticationFailureHandler`입니다.

    - `LoginFailureHandler`를 정의하고 대체하세요.

      ```java
      @Component
      public class LoginFailureHandler implements AuthenticationFailureHandler {
          ...
          @Override
          public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
              AuthenticationException exception) throws IOException, ServletException {
              ...
          }
      }
      ```

        - 인증 실패 시`401 ErrorResponse`로 응답합니다.
    - 설정에 추가하세요.

      ```java
      http
          .formLogin(login -> login
              ...
              .failureHandler(loginFailureHandler)
          )
      ```

- [x] 이제 로그인 처리는 SecurityFilterChain에서 모두 처리되기 때문에 기존에 구현했던 로그인 관련 코드는 제거하세요.

    - `AuthApi.login`,`AuthController.login`
    - `AuthService.login`
    - `LoginRequest`

### 05. 인증 - 세션을 활용한 현재 사용자 정보 조회

> 이전 버전까지의 디스코드잇 프론트엔드에서는 현재 사용자 정보를 브라우저의 세션 스토리지(user-storage)에서 관리해왔습니다.  
> 브라우저의 세션 스토리지는 Javascript로 접근이 가능하기 때문에, XSS(Cross-Site Scripting) 공격에 취약합니다.   
> 따라서 프론트엔드 `2.0.x`부터는 사용자 정보를 브라우저의 메모리에서 관리하도록 변경되었습니다.   
> 하지만, 메모리에 저장된 정보는 브라우저 새로고침 시 모두 삭제됩니다.   
> 따라서 새로고침 시 쿠키에 저장된 세션 ID를 통해 현재 사용자 정보를 조회합니다.

- [x] 세션ID를 통해 사용자의 기본 정보(`UserDto`)를 가져올 수 있도록 API를 정의하세요.
    - API 스펙
        - 엔드포인트:`GET /api/auth/me`
        - 요청:`Header(자동 포함) Cookie: JSESSIONID=…`
        - 응답:`200 UserDto`
    - `SecurityFilterChain`의 필터를 통해 인증에 성공하면`Controller`에서`@AuthenticationPrincipal`를 통해 인증 정보에 접근할
      수 있습니다.

### 06. 인증 - 로그아웃

- Spring Security의 logout 흐름은 그대로 유지하면서 필요한 부분만 대체합니다.

- 이번 미션에서는 2가지 요소를 대체합니다.

    - Logout 처리 URL
    - `LogoutSuccessHandler`
- [x] 로그아웃을 처리할 url을 `/api/auth/logout`로 설정하세요.

  ```java
  http
      .logout(logout -> logout
          .logoutUrl(...)
      )
  ```

- [x] `LogoutSuccessHandler` 컴포넌트를 대체하세요.

    - 디폴트 구현체는 `SimpleUrlLogoutSuccessHandler`입니다.

    - `HttpStatusReturningLogoutSuccessHandler`로 대체하세요.

      ```java
      http
          .logout(logout -> logout
              ...
              .logoutSuccessHandler(...)
          )
      ```

        - `204 Void`응답을 반환하세요.

### 07. 인가 - 권한 정의

- [x] 다음과 같이 권한을 정의하세요.

  ![faef2l3uk-image.png](https://bakey-api.codeit.kr/api/files/resource?root=static&seqId=14408&version=1&directory=/faef2l3uk-image.png&name=faef2l3uk-image.png)

    - 관리자:`ADMIN`
    - 채널 매니저:`CHANNEL_MANAGER`
    - 일반 사용자:`USER`
- [x] 데이터베이스 스키마를 변경하세요.

  ```sql
  CREATE TABLE users (
      ...
      role varchar(20) NOT NULL
  );

  ALTER TABLE users
      ADD role varchar(20) NOT NULL;
  ```

- [x] 회원 가입 시 모든 사용자는 `USER` 권한을 기본 권한으로 설정하세요.

- [x] 사용자 권한을 수정하는 API를 구현하세요.

    - API 스펙
        - 엔드포인트:`PUT /api/auth/role`
        - 요청:`Body UserRoleUpdateRequest`
        - 응답:`200 UserDto`

  ![qu8jij3u4-image.png](https://bakey-api.codeit.kr/api/files/resource?root=static&seqId=14408&version=1&directory=/qu8jij3u4-image.png&name=qu8jij3u4-image.png)

- [x] 애플리케이션 실행 시 `ADMIN` 권한을 가진 어드민 계정이 초기화되도록 구현하세요.

    - 어드민 계정이 없는 경우에만 초기화하세요.
- [x] `DiscodietUserDetails.getAuthorities`를 수정하세요.

### 08. 인가 - 권한 적용

- [x] `authorizeHttpRequests`를 활성화하고, 모든 요청을 인증하도록 설정하세요.

  ```java
  http
      .authorizeHttpRequests(auth -> auth
          .anyRequest().authenticated()
      )
  ```

- [x] 다음의 요청은 인증하지 않도록 설정하세요.

  ```java
  http
      .authorizeHttpRequests(auth -> auth
          ...
          .requestMatchers(...).permitAll()
      )
  ```

    - Csrf Token 발급
    - 회원가입
    - 로그인
    - 로그아웃
    - API가 아닌 요청(Swagger, Actuator 등)
- [x] Method Security를 활성화하세요.

  ```java
  ...
  @EnableMethodSecurity
  public class SecurityConfig {...}
  ```

- [x] Service의 메소드 별로 아래의 조건에 맞게 권한을 수정하세요.

    - 퍼블릭 채널 생성, 수정, 삭제는`CHANNEL_MANAGER`권한을 가져야합니다.
    - 사용자 권한 수정은`ADMIN`권한을 가져야합니다.
- [x] 적절한 권한이 없는 경우 403 응답을 반환하세요.

    - `SecurityFilterChain`

      ```java
      http
          .exceptionHandling(ex -> ex
              .authenticationEntryPoint(...)
              .accessDeniedHandler(...)
          )
      ```

    - `GlobalExceptionHandler`

      ```java
      @ExceptionHandler(MethodArgumentNotValidException.class)
      public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {...}
      ```

- [x] `RoleHierarchy`를 활용해 권한의 계층 구조를 정의하세요.

    - 관리자 > 채널 매니저 > 일반 사용자
        - 관리자 권한은 채널 매니저, 일반 사용자 권한을 포함합니다.
        - 채널 매니저 권한은 일반 사용자 권한을 포함합니다.

  ```java
  @Bean
  public RoleHierarchy roleHierarchy() {...}

  @Bean
  static MethodSecurityExpressionHandler methodSecurityExpressionHandler(
      RoleHierarchy roleHierarchy) {
      DefaultMethodSecurityExpressionHandler handler = new DefaultMethodSecurityExpressionHandler();
      handler.setRoleHierarchy(roleHierarchy);
      return handler;
  }
  ```

### ✏️ 심화 요구사항

### 01. 세션 관리 고도화

- [x] 동일한 계정으로 동시 로그인할 수 없도록 설정하세요.

    - `sessionConcurrency` 설정을 활용하세요.

      ```java
      http
          .sessionManagement(management -> management
              .sessionConcurrency(concurrency -> concurrency
                  ...
              )
          )
      ```

    - 세션의 동일성을 보장하기 위해 `DiscodeitUserDetails`의 `equals()`, `hashcode()` 메소드를 오버라이딩하세요.

      > [공식 문서](https://docs.spring.io/spring-security/reference/servlet/authentication/session-management.html#ns-concurrent-sessions)
      >
      > If you are using a custom implementation of `UserDetails`, ensure you override the *
      *equals()** and **hashCode()** methods. The default `SessionRegistry` implementation in Spring
      Security relies on an in-memory Map that uses these methods to correctly identify and manage
      user sessions. Failing to override them may lead to issues where session tracking and user
      comparison behave unexpectedly.

- [x] 권한이 변경된 사용자가 로그인 상태라면 세션을 무효화하세요.

    - `sessionRegistry`를 활용하세요.

  ```java
  @Bean
  public SecurityFilterChain filterChain(
      ...
      HttpSecurity http,
      SessionRegistry sessionRegistry
  ) {
      http
          .sessionManagement(management -> management
              .sessionConcurrency(concurrency -> concurrency
                  ...
                  .sessionRegistry(sessionRegistry)
              )
          )
      ...
  }

  @Bean
  public SessionRegistry sessionRegistry() {...}
  ```

    - `httpSessionEventPublisher`: HttpSession이 만료된 경우 이벤트를 통해 SessionRegistry의 SessionInformation도
      자동으로 만료하기 위해 필요한 Bean입니다.

  ```java
  @Service
  public class BasicAuthService implements AuthService {
      ...
      private final SessionRegistry sessionRegistry;
      ...
  }
  ```

- [x] UserStatus 엔티티 대신 SessionRegistry를 활용해 사용자의 로그인 여부를 판단하도록 리팩토링하세요.

    - UserStatus 엔티티와 관련된 코드는 모두 삭제하세요.
    - (로그아웃처럼)`HttpSession`만료 시`SessionRegistry`의`SessionInformation`도 자동으로 만료 처리할 수 있도록
      `HttpSessionEventPublisher`를 Bean으로 등록합니다.

  ```java
  @Bean
  public HttpSessionEventPublisher httpSessionEventPublisher() {
      return new HttpSessionEventPublisher();
  }
  ```

### 로그인 고도화 - RememberMe

- [x] 로그인 요청 파라미터(`remember-me`)가 `true`인 경우 세션이 무효화되어도 자동으로 다시 로그인되도록 하세요.

    - 로그인 화면에서 로그인 유지 체크 후 로그인하면`remember-me`파라미터가`true`로 설정되어 요청합니다.

  ![otona45pe-image.png](https://bakey-api.codeit.kr/api/files/resource?root=static&seqId=14411&version=1&directory=/otona45pe-image.png&name=otona45pe-image.png)

    - `remeberMe` 설정을 활용하세요.
    ```java
    http
        .rememberMe(...)
    ```

    - 로그인 상태에서 `JESSIONID` 쿠키를 삭제 후 새로고침했을 때 인증 상태가 유지 되는지 확인해보세요.

      ![aroseetgw-image.png](https://bakey-api.codeit.kr/api/files/resource?root=static&seqId=14411&version=1&directory=/aroseetgw-image.png&name=aroseetgw-image.png)
      => 인증 상태가 유지되는 것 확인 완료

### 03. 권한 적용 고도화

- [x] `SpEL`을 활용해 Method Security 기반 리소스 보호 정책을 강화해보세요.
- 사용자 정보 수정, 삭제는 본인만 할 수 있습니다.
- 메시지 수정, 삭제는 해당 메시지를 작성한 사람만 할 수 있습니다.

## 🔄 주요 변경사항

## 📸 스크린샷

## 🙇🏽‍♂️ 멘토에게

- sprint8 부터 코드잇에서 제공하는 베이스 코드를 사용 중입니다.
