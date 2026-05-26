package com.sprint.mission.discodeit.filter.jwt;

import com.sprint.mission.discodeit.auth.jwt.JwtTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 요청 헤더가 조건에 맞는지 확인
        String token = resolveToken(request);

        // 알맞은 헤더이면 토큰을 검증함
        if (token != null && jwtTokenProvider.validateToken(token)) {
            // 검증 완료된 토큰에서 인증된 Authentication 추출
            Authentication authentication = jwtTokenProvider.getAuthentication(token);

            // SecurityContextHolder에 Authentication 저장해서 요청에 맞게 사용
            SecurityContextHolder.getContext().setAuthentication(authentication);

            log.info("SecurityContext에 '{}' 인증 정보를 저장 완료.", authentication.getName());
        }

        // 다음 필터로 요청 넘김
        filterChain.doFilter(request,response);
    }

    private String resolveToken(HttpServletRequest request){
        // Authorization에 해당하는 헤더만 가져옴
        String bearerToken = request.getHeader("Authorization");

        // 헤더값으로 토큰이 있으면서 Bearer 이 포함된 문자열이라면 해당 부분 자르고 진짜 토큰값들 반환
        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")){
            return bearerToken.substring(7);
        }
        return null;
    }
}
