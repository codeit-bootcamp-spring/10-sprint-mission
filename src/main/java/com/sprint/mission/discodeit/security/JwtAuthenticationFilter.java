package com.sprint.mission.discodeit.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.exception.auth.JwtException;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtRegistry jwtRegistry;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        String token = resolveToken(request);

        if (token != null) {
            try {
                Map<String, Object> claims = jwtTokenProvider.getClaims(token);

                if (!jwtRegistry.hasActiveJwtInformationByAccessToken(token)) {
                    log.debug("JWT 레지스트리에 등록되지 않은 토큰, 익명으로 처리");
                    SecurityContextHolder.clearContext();
                } else {
                    // 토큰 페이로드(sub/username/role)만으로 인증 주체를 구성한다 (DB 재조회 제거).
                    // 권한 변경 즉시 반영은 JwtRegistry 무효화(강제 로그아웃)로 처리된다.
                    UserDto principal = new UserDto(
                            UUID.fromString((String) claims.get("sub")),
                            (String) claims.get("username"),
                            null,
                            null,
                            Role.valueOf((String) claims.get("role")),
                            false);
                    DiscodeitUserDetails userDetails = new DiscodeitUserDetails(principal, null);

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails, null, userDetails.getAuthorities());

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (JwtException e) {
                log.debug("JWT 인증 실패, 익명으로 처리: {}", e.getMessage());
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String bearer = request.getHeader(AUTHORIZATION_HEADER);
        if (bearer != null && bearer.startsWith(BEARER_PREFIX)) {
            return bearer.substring(BEARER_PREFIX.length());
        }
        return null;
    }
}
