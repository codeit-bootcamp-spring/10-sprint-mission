package com.sprint.mission.discodeit.jwt;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import jakarta.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JwtTokenProvider {

    public static final String REFRESH_TOKEN_COOKIE_NAME = "REFRESH_TOKEN";

    private static final String CLAIM_USERNAME = "username";
    private static final String CLAIM_ROLE = "role";
    private static final String CLAIM_TOKEN_TYPE = "tokenType";
    private static final String TOKEN_TYPE_ACCESS = "ACCESS";
    private static final String TOKEN_TYPE_REFRESH = "REFRESH";

    @Value("${discodeit.jwt.secret}")
    private String secret;

    @Value("${discodeit.jwt.access-token-validity-seconds}")
    private long accessTokenValiditySeconds;

    @Value("${discodeit.jwt.refresh-token-validity-seconds}")
    private long refreshTokenValiditySeconds;

    private JWSSigner signer;
    private JWSVerifier verifier;

    @PostConstruct
    void init() throws JOSEException {
        byte[] secretBytes = secret.getBytes(StandardCharsets.UTF_8);
        this.signer = new MACSigner(secretBytes);
        this.verifier = new MACVerifier(secretBytes);
    }

    public String generateAccessToken(UUID userId, String username, String role) {
        return generateToken(userId, username, role, TOKEN_TYPE_ACCESS, accessTokenValiditySeconds);
    }

    public String generateRefreshToken(UUID userId, String username, String role) {
        return generateToken(userId, username, role, TOKEN_TYPE_REFRESH, refreshTokenValiditySeconds);
    }

    public boolean validateToken(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            if (!signedJWT.verify(verifier)) {
                log.warn("JWT 서명 검증 실패");
                return false;
            }
            Date expiration = signedJWT.getJWTClaimsSet().getExpirationTime();
            if (expiration == null || expiration.before(new Date())) {
                log.warn("JWT 만료됨");
                return false;
            }
            return true;
        } catch (ParseException e) {
            log.warn("JWT 파싱 실패: {}", e.getMessage());
            return false;
        } catch (JOSEException e) {
            log.warn("JWT 검증 중 오류: {}", e.getMessage());
            return false;
        }
    }

    public boolean validateRefreshToken(String token) {
        if (!validateToken(token)) {
            return false;
        }
        try {
            String tokenType = (String) parseClaims(token).getClaim(CLAIM_TOKEN_TYPE);
            return TOKEN_TYPE_REFRESH.equals(tokenType);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public String refreshAccessToken(String refreshToken) {
        if (!validateToken(refreshToken)) {
            throw new IllegalArgumentException("유효하지 않은 리프레시 토큰입니다.");
        }
        JWTClaimsSet claims = parseClaims(refreshToken);
        String tokenType = (String) claims.getClaim(CLAIM_TOKEN_TYPE);
        if (!TOKEN_TYPE_REFRESH.equals(tokenType)) {
            throw new IllegalArgumentException("리프레시 토큰이 아닙니다.");
        }

        UUID userId = UUID.fromString(claims.getSubject());
        String username = (String) claims.getClaim(CLAIM_USERNAME);
        String role = (String) claims.getClaim(CLAIM_ROLE);
        return generateAccessToken(userId, username, role);
    }

    public JWTClaimsSet parseClaims(String token) {
        try {
            return SignedJWT.parse(token).getJWTClaimsSet();
        } catch (ParseException e) {
            throw new IllegalArgumentException("토큰 파싱에 실패했습니다.", e);
        }
    }

    public UUID getUserId(String token) {
        return UUID.fromString(parseClaims(token).getSubject());
    }

    public String getUsername(String token) {
        return (String) parseClaims(token).getClaim(CLAIM_USERNAME);
    }

    public String getRole(String token) {
        return (String) parseClaims(token).getClaim(CLAIM_ROLE);
    }

    public Instant getExpiration(String token) {
        return parseClaims(token).getExpirationTime().toInstant();
    }

    private String generateToken(UUID userId, String username, String role, String tokenType,
        long validitySeconds) {
        Instant now = Instant.now();
        Instant expiration = now.plus(validitySeconds, ChronoUnit.SECONDS);

        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
            .subject(userId.toString())
            .claim(CLAIM_USERNAME, username)
            .claim(CLAIM_ROLE, role)
            .claim(CLAIM_TOKEN_TYPE, tokenType)
            .issueTime(Date.from(now))
            .expirationTime(Date.from(expiration))
            .build();

        SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);
        try {
            signedJWT.sign(signer);
        } catch (JOSEException e) {
            throw new IllegalStateException("JWT 서명 생성에 실패했습니다.", e);
        }
        return signedJWT.serialize();
    }
}
