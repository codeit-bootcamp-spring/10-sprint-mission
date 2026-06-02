package com.sprint.mission.discodeit.auth.jwt;

import com.sprint.mission.discodeit.auth.DiscodeitUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final DiscodeitUserDetailsService userDetailsService;
    private final JwtRegistry jwtRegistry;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 1. 요청 헤더에서 JWT 토큰 추출
        String jwt = resolveToken(request);

        // 2. 토큰이 존재하고, 유효하며, Registry에 활성화된 경우에만 인증 처리
        try {
            if (StringUtils.hasText(jwt) && jwtTokenProvider.validateToken(jwt) && jwtRegistry.hasActiveJwtInformationByAccessToken(jwt)) {
                
                // 3. 토큰에서 사용자 정보(username) 추출
                String username = jwtTokenProvider.getUsername(jwt);

                // 4. DB에서 사용자 상세 정보 및 권한 조회
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                // 5. UsernamePasswordAuthenticationToken 생성 (비밀번호는 null 처리, 권한은 userDetails에서 가져옴)
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

                // 6. SecurityContext에 인증 정보 저장 (인증 완료 처리)
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            // 토큰 검증 실패 (만료, 조작 등) 시 인증 처리를 하지 않고 넘어갑니다.
            // 필요에 따라 로깅을 추가할 수 있습니다.
        }

        // 7. 다음 필터로 요청 전달 (요청 당 한 번만 실행됨)
        filterChain.doFilter(request, response);
    }

    // 헤더에서 "Bearer " 접두사를 제외한 순수 토큰 값만 추출
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
