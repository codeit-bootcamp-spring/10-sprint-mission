package com.sprint.mission.discodeit.security.jwt.provider;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.sprint.mission.discodeit.security.auth.DiscodeitUserDetails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.stream.Collectors;

/*
    JwtTokenProvider
    ----------------
    JWT 토큰 발급 및 검증
 */
@Component
public class JwtTokenProvider {

    private static final String AUTHORITIES_KEY = "auth";       // 권한 정보 키 값

    private final int accessTokenExpirationMinutes;             // 액세스 토큰 유효 시간
    private final int refreshTokenExpirationMinutes;            // 리프레시 토큰 유효 시간

    private final JWSSigner jwsSigner;                          // JWT 서명 객체
    private final JWSVerifier jwsVerifier;                      // JWT 검증 객체

    public JwtTokenProvider(
            @Value("${jwt.key}") String secretKey,
            @Value("${jwt.access-token-expiration-minutes}") int accessTokenExpirationMinutes,
            @Value("${jwt.refresh-token-expiration-minutes}") int refreshTokenExpirationMinutes) throws JOSEException {

        this.accessTokenExpirationMinutes = accessTokenExpirationMinutes;
        this.refreshTokenExpirationMinutes = refreshTokenExpirationMinutes;

        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        this.jwsSigner = new MACSigner(keyBytes);
        this.jwsVerifier = new MACVerifier(keyBytes);
    }

    // 액세스 토큰 발급
    public String generateAccessToken(Authentication authentication) {
        // 사용자 정보 조회
        DiscodeitUserDetails userDetails = (DiscodeitUserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        String userId = userDetails.getUserDto().id().toString();

        // 인증 정보 내 권한 추출
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        return createToken(username, userId, authorities, accessTokenExpirationMinutes);
    }

    // 리프레시 토큰 발급
    public String generateRefreshToken(Authentication authentication) {
        DiscodeitUserDetails userDetails = (DiscodeitUserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        String userId = userDetails.getUserDto().id().toString();

        return createToken(username, userId, null, refreshTokenExpirationMinutes);
    }

    // 토큰 생성
    private String createToken(String subject, String userId, String authorities, int expirationMinutes) {
        try {
            // 현재 시간 및 만료 시간 계산
            Date now = new Date();
            Date expiration = new Date(now.getTime() + (long) expirationMinutes * 60 * 1000);

            // JWT 페이로드 구성
            JWTClaimsSet.Builder builder = new JWTClaimsSet.Builder()
                    .subject(subject)                // 사용자 인증 정보
                    .claim("userId", userId)   // 사용자 ID
                    .issueTime(now)                  // 발급 시각
                    .expirationTime(expiration)      // 만료 시각
                    .issuer("discodeit");            // 발급자

            // 인증 정보가 없을 경우, 권한 정보 추가 (auth: USER)
            if (authorities != null) {
                builder.claim(AUTHORITIES_KEY, authorities);
            }

            // JWT 페이로드 + JWT 헤더
            SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), builder.build());
            // JWT 토큰 서명: 데이터가 위조되지 않았음을 보증
            signedJWT.sign(jwsSigner);

            // 문자열 형태의 토큰 반환
            return signedJWT.serialize();
        } catch (JOSEException e) {
            throw new RuntimeException("Failed generate JWT Token", e);
        }
    }

    // 토큰에서 식별자 (UUID) 추출
    public String getUserId(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            return signedJWT.getJWTClaimsSet().getStringClaim("userId");
        } catch (ParseException e) {
            throw new RuntimeException("Failed JWT Parsing", e);
        }
    }

    // 토큰에서 인증 정보 추출: 토큰이 유효하면 시큐리티 인증 객체 반환
    public Authentication getAuthentication(String token) {
        try {
            // JWT 토큰 객체화 및 JWT 페이로드 추출
            SignedJWT signedJWT = SignedJWT.parse(token);
            JWTClaimsSet claims = signedJWT.getJWTClaimsSet();

            // 권한 정보 추출
            String authoritiesStr = claims.getStringClaim(AUTHORITIES_KEY);
            Collection<? extends GrantedAuthority> authorities =
                    (authoritiesStr != null && !authoritiesStr.isBlank())
                            ? Arrays.stream(authoritiesStr.split(","))
                                    .map(SimpleGrantedAuthority::new)
                                    .collect(Collectors.toList())
                            // 권한이 없는 경우, 빈 리스트 반환
                            : Collections.emptyList();

            // 사용자 정보 객체 생성
            UserDetails principal = new User(claims.getSubject(), "", authorities);
            return new UsernamePasswordAuthenticationToken(principal, null, authorities);
        } catch (ParseException e) {
            throw new RuntimeException("Failed JWT Parsing", e);
        }
    }

    // 토큰 검증: 서명 확인 및 만료 시간 체크
    public boolean validateToken(String token) {
        try {
            // JWT 토큰 객체화
            SignedJWT signedJWT = SignedJWT.parse(token);

            // JWT 토큰 검증
            if (!signedJWT.verify(jwsVerifier)){
                return false;
            }

            // 유효 시간 만료 여부 검증
            Date expirationTime = signedJWT.getJWTClaimsSet().getExpirationTime();
            return expirationTime != null && new Date().before(expirationTime);
        } catch (Exception e) {
            return false;
        }
    }
}