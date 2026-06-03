package com.sprint.mission.discodeit.security.jwt.filter;

import com.sprint.mission.discodeit.security.auth.DiscodeitUserDetails;
import com.sprint.mission.discodeit.security.jwt.provider.JwtTokenProvider;
import com.sprint.mission.discodeit.security.jwt.registry.JwtRegistry;
import com.sprint.mission.discodeit.service.basic.DiscodeitUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

/*
    JwtAuthenticationFilter
    -----------------------
    매 API 요청마다 한 번씩 실행되는, JWT 토큰 검사 보안 필터
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtRegistry jwtRegistry;

    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 요청 헤더로부터 JWT 토큰 추출
        String token = resolveToken(request);

        // JWT 토큰 검증
        if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)) {
            // 해당 토큰이 사용자가 가장 최근에 발급받은 액세스 토큰인지 확인
            if (jwtRegistry.hasActiveJwtInformationByAccessToken(token)) {
                // 토큰으로부터 인증 정보 추출
                String userIdStr = jwtTokenProvider.getUserId(token);
                DiscodeitUserDetails userDetails =
                        (DiscodeitUserDetails) ((DiscodeitUserDetailsService) userDetailsService).loadUserById(userIdStr);

                // 시큐리티 컨텍스트(SecurityContextHolder)에 인증된 사용자 정보 저장
                Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                // 토큰 만료 시간은 유효하지만, 해당 토큰을 현재 사용자가 사용하지 않는 경우
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token invalidated");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    // HTTP 요청 헤더로부터 JWT 토큰 추출
    private String resolveToken(HttpServletRequest request) {
        // 요청 헤더 (Authorization) 추출
        String bearerToken = request.getHeader("Authorization");

        // Bearer 토큰 포함 여부 확인
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        return null;
    }
}