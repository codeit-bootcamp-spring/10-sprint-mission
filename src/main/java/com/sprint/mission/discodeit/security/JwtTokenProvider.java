package com.sprint.mission.discodeit.security;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Component
public class JwtTokenProvider {

    private final SecretKey secretKey;
    private final long accessTokenExpiration;
    private final long refreshTokenExpiration;

    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey,
                            @Value("${jwt.access-token-expiration}") long accessTokenExpiration,
                            @Value("${jwt.refresh-token-expiration}") long refreshTokenExpiration) {
        this.secretKey = new SecretKeySpec(
                secretKey.getBytes(StandardCharsets.UTF_8),
                "HmacSHA256");
        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
    }

    // 토큰 생성
    public String generateToken(UUID userId, String username, String role) {
        try {
            Instant now = Instant.now();
            Instant expiry = now.plusMillis(accessTokenExpiration);

            // Payload 구성 (Claims)
            JWTClaimsSet claims = new JWTClaimsSet.Builder()
                    .subject(userId.toString())          // 사용자 ID
                    .claim("username", username)          // 사용자명
                    .claim("role", role)                  // 권한
                    .issueTime(Date.from(now))           // 발급 시간
                    .expirationTime(Date.from(expiry))   // 만료 시간
                    .build();

            // 서명 방식 설정 (HMAC + SHA-256)
            JWSSigner signer = new MACSigner(secretKey);

            // JWT 생성
            SignedJWT signedJWT = new SignedJWT(
                    new JWSHeader(JWSAlgorithm.HS256),
                    claims
            );
            signedJWT.sign(signer);

            return signedJWT.serialize(); // "header.payload.signature" 문자열 반환

        } catch (JOSEException e) {
            throw new RuntimeException("JWT 생성 실패", e);
        }
    }

    // 토큰 유효성 검증
    public boolean validateToken(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);

            // 서명 검증 (비밀키로)
            JWSVerifier verifier = new MACVerifier(secretKey);
            if (!signedJWT.verify(verifier)) {
                return false;
            }

            // 만료 시간 검증
            Date expiry = signedJWT.getJWTClaimsSet().getExpirationTime();
            return expiry != null && expiry.after(new Date());

        } catch (Exception e) {
            log.warn("JWT 검증 실패: {}", e.getMessage());
            return false;
        }
    }

    // 토큰에서 사용자 ID 추출
    public UUID getUserId(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            String subject = signedJWT.getJWTClaimsSet().getSubject();
            return UUID.fromString(subject);
        } catch (Exception e) {
            throw new RuntimeException("토큰에서 사용자 ID 추출 실패", e);
        }
    }

    // 토큰에서 권한 추출
    public String getRole(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            return (String) signedJWT.getJWTClaimsSet().getClaim("role");
        } catch (Exception e) {
            throw new RuntimeException("토큰에서 권한 추출 실패", e);
        }
    }

    public String generateRefreshToken(UUID userId) {
        try {
            Instant now = Instant.now();
            Instant expiry = now.plusMillis(refreshTokenExpiration);

            JWTClaimsSet claims = new JWTClaimsSet.Builder()
                    .subject(userId.toString())
                    .issueTime(Date.from(now))
                    .expirationTime(Date.from(expiry))
                    .build();

            JWSSigner signer = new MACSigner(secretKey);
            SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claims);
            signedJWT.sign(signer);

            return signedJWT.serialize();
        } catch (JOSEException e) {
            throw new RuntimeException("리프레시 토큰 생성 실패", e);
        }
    }
}
