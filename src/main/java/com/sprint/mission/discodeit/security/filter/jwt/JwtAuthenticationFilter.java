package com.sprint.mission.discodeit.security.filter.jwt;

import com.nimbusds.jwt.JWTClaimsSet;
import com.sprint.mission.discodeit.security.jwt.JwtTokenProvider;
import com.sprint.mission.discodeit.security.registry.JwtRegistry;
import com.sprint.mission.discodeit.security.userdetails.DiscodeitUserDetails;
import com.sprint.mission.discodeit.security.userdetails.DiscodeitUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// 요청의 Access Token을 검증하고 Spring Security에 인증 객체를 등록하는 Filter
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtRegistry jwtRegistry;

    private final DiscodeitUserDetailsService discodeitUserDetailsService;

    // Bearer Access Token이 있으면 인증 시도
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException
    {
        String authorizationHeader = request.getHeader("Authorization");

        // Authorization 헤더에 Bearer 토큰이 없을 경우, JWT 기반 토큰 인증을 진행하지 않고 다음 filter로 넘김
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            // 다음 filter로 넘김
            filterChain.doFilter(request, response);
            return;
        }

        // "Bearer "을 제외한 나머지(Access Token)를 가져옴
        String accessToken = authorizationHeader.substring("Bearer ".length());

        try {
            // JWT 검증, Access Token 여부 확인 후 claims 반환
            // 예외 발생 시, 실패 원인 추가를 위해 "try...catch"문 안에 포함
            JWTClaimsSet jwtClaimsSet = jwtTokenProvider.getAndValidateAccessToken(accessToken);

            // Registry에서 Access Token이 Active인지 확인
            if (!jwtRegistry.hasActiveJwtInformationByAccessToken(accessToken)) {
                throw new BadCredentialsException("Active Access Token이 아닙니다.");
            }

            // claims에서 username 조회
            String username = jwtClaimsSet.getStringClaim("username");

            // claims로 사용자 조회
            DiscodeitUserDetails userDetails =
                    (DiscodeitUserDetails) discodeitUserDetailsService.loadUserByUsername(username);

            // 검증된 사용자 정보 + 권한으로 인증 객체 생성
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );

            // 인증 객체를 SecurityContext에 저장하여 현재 요청을 인증된 상태로 처리
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        } catch (Exception e) {
            // 토큰 검증이나 사용자 조회에 실패할 경우
            // 현재 요청의 인증 상태를 제거하고, 미인증 상태(인증된 사용자X)로 다음 filter로 넘김
            SecurityContextHolder.clearContext();
            throw new BadCredentialsException("유효하지 않은 Access Token입니다.", e);
        }
        // 다음 filter로 이동
        filterChain.doFilter(request, response);
    }


}
