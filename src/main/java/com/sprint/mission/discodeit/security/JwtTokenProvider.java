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

import java.text.ParseException;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Component
public class JwtTokenProvider {

    private final JWSSigner signer;
    private final JWSVerifier verifier;
    private final long accessTokenExpiration;
    private final long refreshTokenExpiration;

    public JwtTokenProvider(
            @Value("${discodeit.jwt.secret}") String secret,
            @Value("${discodeit.jwt.access-token-expiration}") long accessTokenExpiration,
            @Value("${discodeit.jwt.refresh-token-expiration}") long refreshTokenExpiration)
            throws JOSEException {
        byte[] secretBytes = secret.getBytes();
        this.signer = new MACSigner(secretBytes);
        this.verifier = new MACVerifier(secretBytes);
        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
    }

    // Access Token 발급
    public String generateAccessToken(UUID userId, String username, String role) {
        return generateToken(userId, username, role, accessTokenExpiration);
    }

    // Refresh Token 발급
    public String generateRefreshToken(UUID userId, String username) {
        return generateToken(userId, username, null, refreshTokenExpiration);
    }

    // 토큰 생성 공통 메서드
    private String generateToken(UUID userId, String username, String role, long expiration) {
        try {
            Date now = new Date();
            JWTClaimsSet.Builder claimsBuilder = new JWTClaimsSet.Builder()
                    .subject(userId.toString())
                    .issueTime(now)
                    .expirationTime(new Date(now.getTime() + expiration));

            if (username != null) claimsBuilder.claim("username", username);
            if (role != null) claimsBuilder.claim("role", role);

            SignedJWT signedJWT = new SignedJWT(
                    new JWSHeader(JWSAlgorithm.HS256),
                    claimsBuilder.build()
            );
            signedJWT.sign(signer);
            return signedJWT.serialize();

        } catch (JOSEException e) {
            throw new RuntimeException("토큰 생성 실패", e);
        }
    }

    // 토큰 유효성 검사
    public boolean validate(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            if (!signedJWT.verify(verifier)) return false;

            Date expiration = signedJWT.getJWTClaimsSet().getExpirationTime();
            return expiration != null && expiration.after(new Date());

        } catch (ParseException | JOSEException e) {
            log.warn("유효하지 않은 JWT 토큰: {}", e.getMessage());
            return false;
        }
    }

    // 토큰에서 userId 추출
    public UUID getUserId(String token) {
        return UUID.fromString(getClaims(token).getSubject());
    }

    // 토큰에서 username 추출
    public String getUsername(String token) {
        try {
            return getClaims(token).getStringClaim("username");
        } catch (ParseException e) {
            throw new RuntimeException("토큰 파싱 실패", e);
        }
    }

    // 토큰에서 role 추출
    public String getRole(String token) {
        try {
            return getClaims(token).getStringClaim("role");
        } catch (ParseException e) {
            throw new RuntimeException("토큰 파싱 실패", e);
        }
    }

    // Claims 파싱
    private JWTClaimsSet getClaims(String token) {
        try {
            return SignedJWT.parse(token).getJWTClaimsSet();
        } catch (ParseException e) {
            throw new RuntimeException("토큰 파싱 실패", e);
        }
    }
}
