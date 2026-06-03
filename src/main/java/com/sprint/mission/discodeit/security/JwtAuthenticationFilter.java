package com.sprint.mission.discodeit.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final DiscodeitUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // 1. Authorization 헤더에서 Bearer 토큰 추출
        String token = resolveToken(request);

        // 2. 토큰이 있고 유효한 경우에만 인증 처리
        if (token != null && jwtTokenProvider.validateToken(token)) {

            // 3. 토큰에서 userId 꺼내서 UserDetails 조회
            UUID userId = jwtTokenProvider.getUserId(token);
            UserDetails userDetails = userDetailsService.loadUserById(userId);

            // 4. 인증 객체 생성 후 SecurityContext에 저장
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,                          // credentials (비밀번호) - 이미 인증됐으니 불필요
                            userDetails.getAuthorities()   // 권한 목록
                    );
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // 5. 다음 필터로 이동
        filterChain.doFilter(request, response);
    }

    // Authorization: Bearer <token> 에서 토큰만 추출
    private String resolveToken(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        if (StringUtils.hasText(bearer) && bearer.startsWith("Bearer ")) {
            return bearer.substring(7); // "Bearer " 이후 문자열
        }
        return null;
    }
}
