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
import com.sprint.mission.discodeit.exception.auth.ExpiredJwtException;
import com.sprint.mission.discodeit.exception.auth.InvalidJwtSignatureException;
import com.sprint.mission.discodeit.exception.auth.JwtGenerationException;
import com.sprint.mission.discodeit.exception.auth.MalformedJwtException;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.Date;
import java.util.Map;

@Component
public class JwtTokenProvider {

    public static final String REFRESH_TOKEN_COOKIE_NAME = "REFRESH_TOKEN";

    @Getter
    @Value("${discodeit.jwt.key}")
    private String secretKey;

    @Getter
    @Value("${discodeit.jwt.access-token-expiration-minutes}")
    private int accessTokenExpirationMinutes;

    @Getter
    @Value("${discodeit.jwt.refresh-token-expiration-minutes}")
    private int refreshTokenExpirationMinutes;

    // HS256 서명에는 최소 256비트(32바이트) 키가 필요하다. 짧은/빈 키를 첫 로그인이 아닌 기동 시점에 차단한다.
    @PostConstruct
    public void validateSecretKey() {
        if (secretKey == null || secretKey.getBytes(StandardCharsets.UTF_8).length < 32) {
            throw new IllegalStateException(
                    "JWT 서명 키(discodeit.jwt.key)는 HS256을 위해 최소 32바이트(256비트) 이상이어야 합니다.");
        }
    }

    public String generateAccessToken(Map<String, Object> claims, String subject) {
        try {
            JWSSigner signer = new MACSigner(secretKey.getBytes(StandardCharsets.UTF_8));

            Date expiration = new Date(System.currentTimeMillis() + accessTokenExpirationMinutes * 60L * 1000);

            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                    .subject(subject)
                    .claim("username", claims.get("username"))
                    .claim("role", claims.get("role"))
                    .expirationTime(expiration)
                    .issueTime(new Date())
                    .issuer("discodeit")
                    .build();

            SignedJWT signedJWT = new SignedJWT(
                    new JWSHeader(JWSAlgorithm.HS256),
                    claimsSet
            );

            signedJWT.sign(signer);
            return signedJWT.serialize();
        } catch (JOSEException e) {
            throw new JwtGenerationException(e.getMessage());
        }
    }

    public String generateRefreshToken(String subject) {
        try {
            JWSSigner signer = new MACSigner(secretKey.getBytes(StandardCharsets.UTF_8));

            Date expiration = new Date(System.currentTimeMillis() + refreshTokenExpirationMinutes * 60L * 1000);

            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                    .subject(subject)
                    .expirationTime(expiration)
                    .issueTime(new Date())
                    .issuer("discodeit")
                    .build();

            SignedJWT signedJWT = new SignedJWT(
                    new JWSHeader(JWSAlgorithm.HS256),
                    claimsSet
            );

            signedJWT.sign(signer);
            return signedJWT.serialize();
        } catch (JOSEException e) {
            throw new JwtGenerationException(e.getMessage());
        }
    }

    public Map<String, Object> getClaims(String token) {
        SignedJWT signedJWT;
        try {
            signedJWT = SignedJWT.parse(token);
        } catch (ParseException e) {
            // 토큰 형식 자체가 깨진 경우
            throw new MalformedJwtException();
        }

        try {
            JWSVerifier verifier = new MACVerifier(secretKey.getBytes(StandardCharsets.UTF_8));
            if (!signedJWT.verify(verifier)) {
                throw new InvalidJwtSignatureException();
            }

            JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();
            Date expiration = claimsSet.getExpirationTime();
            if (expiration != null && expiration.before(new Date())) {
                throw new ExpiredJwtException();
            }

            return claimsSet.getClaims();
        } catch (JOSEException | ParseException e) {
            // 서명 검증·클레임 파싱 중 라이브러리 예외 → 형식 오류로 간주
            // (InvalidJwtSignature/ExpiredJwt 예외는 RuntimeException이라 여기서 잡히지 않고 전파된다)
            throw new MalformedJwtException();
        }
    }
}
