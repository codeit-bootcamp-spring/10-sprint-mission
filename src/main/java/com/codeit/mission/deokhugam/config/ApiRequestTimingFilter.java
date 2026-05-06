package com.codeit.mission.deokhugam.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
@Order(Integer.MIN_VALUE) // 체인에서 가장 앞 위치
public class ApiRequestTimingFilter extends OncePerRequestFilter {

  // Redact 해야 할 민감 키워드들
  private static final Set<String> SENSITIVE_KEYS = Set.of("token", "key", "password", "secret", "authorization");

  @Override
  protected void doFilterInternal(HttpServletRequest request,
      HttpServletResponse response,
      FilterChain filterChain
  ) throws ServletException, IOException {
    long start = System.nanoTime();

    try{
      filterChain.doFilter(request, response);
    } finally {
      long elapsedMs = (System.nanoTime() - start) / 1_000_000;

      if(request.getRequestURI().startsWith("/api")) {
        log.info("[API_TIMING] method={}, uri={}, query={}, status={}, elapsedMs={}",
            request.getMethod(),
            request.getRequestURI(),
            sanitizeQueryString(request.getQueryString()),
            response.getStatus(),
            elapsedMs
            );
      }
    }
  }

  // 로그 내 키가 민감 키워드일 시 REDACT하는 메서드
  private String sanitizeQueryString(String queryString){
    if(queryString == null){
      return null;
    }
    return Arrays.stream(queryString.split("&")) // 파라미터를 구분하기 위헤 &로 스플릿
        .map(param -> {
          String[] kv = param.split("=", 2); // 키 밸류 값을 구분하기 위해 =로 스플릿 ("password" = "1234") -> {"password", "1234"}
          if(kv.length == 2 && SENSITIVE_KEYS.contains(kv[0].toLowerCase())){   // 키 값이 민감 키워드를 내포하고 있다면 REDACT
            return kv[0] + "=****";
          }
          return param;
        })
        .collect(Collectors.joining("&")); // 다시 &로 JOIN
  }


}
