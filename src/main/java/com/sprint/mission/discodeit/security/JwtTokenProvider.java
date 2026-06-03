package com.sprint.mission.discodeit.security;

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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtTokenProvider {

    public static final String REFRESH_TOKEN_COOKIE_NAME = "REFRESH_TOKEN";

    @Value("${jwt.secret-key}")
    private String secretKey;

    @Value("${jwt.access-token-expiration-minutes}")
    private int accessTokenExpirationMinutes;

    @Value("${jwt.refresh-token-expiration-minutes}")
    private int refreshTokenExpirationMinutes;

    @Value("${jwt.issuer:discodeit}")
    private String issuer;

    public String generateAccessToken(Map<String, Object> claims, String subject) {
        return generateToken(claims, subject, accessTokenExpirationMinutes);
    }

    public String generateRefreshToken(Map<String, Object> claims, String subject) {
        return generateToken(claims, subject, refreshTokenExpirationMinutes);
    }

    public Map<String, Object> getClaims(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            JWSVerifier verifier = new MACVerifier(secretKey.getBytes(StandardCharsets.UTF_8));

            if (!signedJWT.verify(verifier)) {
                throw new RuntimeException("JWT 검증 실패");
            }

            return signedJWT.getJWTClaimsSet().getClaims();
        } catch (Exception e) {
            throw new RuntimeException("JWT 파싱 실패", e);
        }
    }

    public boolean validateToken(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            JWSVerifier verifier = new MACVerifier(secretKey.getBytes(StandardCharsets.UTF_8));

            if (!signedJWT.verify(verifier)) {
                log.debug("JWT 서명이 유효하지 않습니다.");
                return false;
            }

            Date expiration = signedJWT.getJWTClaimsSet().getExpirationTime();
            if (expiration == null || expiration.before(new Date())) {
                log.debug("JWT 토큰이 만료되었습니다.");
                return false;
            }

            return true;
        } catch (Exception e) {
            log.debug("JWT 유효성 검사 실패: {}", e.getMessage());
            return false;
        }
    }

    public Authentication getAuthentication(String token) {
        Map<String, Object> claims = getClaims(token);

        String subject = (String) claims.get("sub");
        Object rolesObj = claims.get("roles");

        Collection<GrantedAuthority> authorities;
        if (rolesObj instanceof String rolesStr && !rolesStr.isBlank()) {
            authorities = Arrays.stream(rolesStr.split(","))
                    .map(String::trim)
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
        } else {
            authorities = List.of();
        }

        User principal = new User(subject, "", authorities);

        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    private String generateToken(Map<String, Object> claims, String subject, int expirationMinutes) {
        try {
            JWSSigner signer = new MACSigner(secretKey.getBytes(StandardCharsets.UTF_8));

            Date expiration = new Date(System.currentTimeMillis() + (long) expirationMinutes * 60 * 1000);

            JWTClaimsSet.Builder builder = new JWTClaimsSet.Builder()
                    .subject(subject)
                    .issueTime(new Date())
                    .expirationTime(expiration)
                    .issuer(issuer);

            if (claims != null) {
                claims.forEach(builder::claim);
            }

            SignedJWT signedJWT = new SignedJWT(
                    new JWSHeader(JWSAlgorithm.HS256),
                    builder.build());

            signedJWT.sign(signer);
            return signedJWT.serialize();
        } catch (Exception e) {
            throw new RuntimeException("JWT 발급 실패", e);
        }
    }
}
