package com.sprint.mission.discodeit.security.jwt;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.sprint.mission.discodeit.config.jwt.JwtProperties;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.exception.security.InvalidAccessTokenException;
import com.sprint.mission.discodeit.exception.security.InvalidJwtTokenException;
import com.sprint.mission.discodeit.exception.security.InvalidRefreshTokenException;
import com.sprint.mission.discodeit.exception.security.JwtTokenCreateFailedException;
import com.sprint.mission.discodeit.security.userdetails.DiscodeitUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.time.Instant;
import java.util.Date;

// JWT 토큰 발급, 재발급, 검증을 담당하는 컴포넌트
@Component
@Slf4j
@RequiredArgsConstructor
public class JwtTokenProvider {

    // claims의 토큰 타입 구분을 위한 문자열
    private static final String TOKEN_TYPE = "token_type";
    private static final String ACCESS_TOKEN_TYPE = "access_token";
    private static final String REFRESH_TOKEN_TYPE = "refresh_token";

    private final JwtProperties jwtProperties;

    // 인증된 사용자 정보를 담은 Access Token 생성
    public String generateAccessToken(DiscodeitUserDetails discodeitUserDetails) {
        Instant now = Instant.now();
        UserDto userDto = discodeitUserDetails.getUserDto();

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(userDto.id().toString())
                .claim(TOKEN_TYPE, ACCESS_TOKEN_TYPE)
                .claim("email", userDto.email())
                .claim("username", userDto.username())
                .claim("role", userDto.role())
                .issueTime(Date.from(now))
                .expirationTime(Date.from(
                        now.plus(jwtProperties.getAccessTokenExpirationTime())
                ))
                .build();

        return createJwtToken(jwtClaimsSet);
    }

    // 인증된 사용자의 식별자만 담은 Refresh Token 생성
    public String generateRefreshToken(DiscodeitUserDetails discodeitUserDetails) {
        Instant now = Instant.now();
        UserDto userDto = discodeitUserDetails.getUserDto();

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(userDto.id().toString())
                .claim(TOKEN_TYPE, REFRESH_TOKEN_TYPE)
                .issueTime(Date.from(now))
                .expirationTime(Date.from(
                        now.plus(jwtProperties.getRefreshTokenExpirationTime())
                ))
                .build();

        return createJwtToken(jwtClaimsSet);
    }

    // 유효한 Refresh Token에서 가져온 JWTClaimsSet과 사용자 정보로 Access Token을 재발급
    public String refreshAccessToken(
            JWTClaimsSet jwtClaimsSet,
            DiscodeitUserDetails discodeitUserDetails
    ) {
        String userId = discodeitUserDetails.getUserDto().id().toString();

        // Refresh Token의 subject와 현재 사용자의 id가 다를 경우, 예외 발생
        if (!userId.equals(jwtClaimsSet.getSubject())) {
            throw new InvalidRefreshTokenException();
        }

        // 인증된 사용자 정보를 담은 Access Token 생성
        return generateAccessToken(discodeitUserDetails);
    }

    // JWT의 서명과 만료 시간을 검증해 JWT 토큰 유효 여부 확인
    public boolean validateToken(String token) {
        // 토큰이 없거나 공백일 경우, 유효하지 않은 토큰(false)
        if (token == null || token.isBlank()) {
            return false;
        }

        try {
            // JWT 토큰의 서명과 만료 시간을 검증
            getAndVerifyToken(token);
            return true;
        } catch (InvalidJwtTokenException e) {
            // 토큰 파싱, 서명 검증, 만료 검증 중 하나라도 실패할 경우, 유효하지 않은 토큰(false)
            return false;
        }
    }

    // Access Token 검증 후 claims 반환
    public JWTClaimsSet getAndValidateAccessToken(String token) {
        // 토큰이 없거나 공백일 경우, 유효하지 않은 토큰
        if (token == null || token.isBlank()) {
            throw new InvalidJwtTokenException("토큰이 존재하지 않습니다.");
        }

        // JWT 토큰의 서명과 만료 시간을 검증하고 claims를 반환
        JWTClaimsSet jwtClaimsSet = getAndVerifyToken(token);

        // claims의 토큰 타입이 Access Token인지 검증
        validateAccessToken(jwtClaimsSet);

        return jwtClaimsSet;
    }

    // Refresh Token 검증 후 claims 반환
    public JWTClaimsSet getAndValidateRefreshToken(String token) {
        // 토큰이 없거나 공백일 경우, 유효하지 않은 토큰
        if (token == null || token.isBlank()) {
            throw new InvalidJwtTokenException("토큰이 존재하지 않습니다.");
        }

        // JWT 토큰의 서명과 만료 시간을 검증하고 claims를 반환
        JWTClaimsSet jwtClaimsSet = getAndVerifyToken(token);

        // claims의 토큰 타입이 Refresh Token인지 검증
        validateRefreshToken(jwtClaimsSet);

        return jwtClaimsSet;
    }

    // JWT 문자열에서 subject 추출
    public String getSubject(String token) {
        try {
          SignedJWT signedJWT = SignedJWT.parse(token);

          return signedJWT.getJWTClaimsSet().getSubject();
        } catch (ParseException e) {
            throw new InvalidJwtTokenException("Jwt에서 subject 추출에 실패했습니다.", e);
        }
    }

    // claims를 HS256 방식으로 서명해 JWT 문자열로 직렬화
    private String createJwtToken(JWTClaimsSet jwtClaimsSet) {
        try {
            // 현재 가지고 있는 secret key와 HMAC 서명을 사용해 JWT에 서명할 객체 생성
            JWSSigner jwsSigner = new MACSigner(getJwtSecretKeyBytes());

            // HS256 알고르즘 정보가 담긴 header와 사용자 정보가 담긴 claims로 서명 가능한 JWT 객체 생성
            SignedJWT signedJWT = new SignedJWT(
                    new JWSHeader(JWSAlgorithm.HS256),
                    jwtClaimsSet
            );

            // 현재 가지고 있는 secret key를 사용해 JWT에 HMAC 서명 추가
            signedJWT.sign(jwsSigner);

            // 서명된 JWT를 직렬화
            return signedJWT.serialize();
        } catch (JOSEException e) {
            // JWT 서명 생성 과정에서 오류가 발생할 경우, 서버 내부의 토큰 생성 실패로 보고 예외 발생
            throw new JwtTokenCreateFailedException("JWT 생성에 실패했습니다.", e);
        }
    }

    // JWT 문자열을 파싱한 뒤 서명과 만료 시간을 검증하고 claims를 반환
    private JWTClaimsSet getAndVerifyToken(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);

            // JWT 서명이 현재 가지고 있는 secret key에 HMAC 서명을 사용해 만들어졌는지 확인
            JWSVerifier jwsVerifier = new MACVerifier(getJwtSecretKeyBytes());

            // JWT 서명이 secret key로 검증되지 않을 경우, 예외 발생
            if (!signedJWT.verify(jwsVerifier)) {
                // 변조되었거나 신뢰할 수 없는 토큰
                throw new InvalidJwtTokenException("JWT 서명이 유효하지 않습니다.");
            }

            JWTClaimsSet jwtClaimsSet = signedJWT.getJWTClaimsSet();
            Date expirationTime = jwtClaimsSet.getExpirationTime();

            // 만료 시간이 없거나 이미 지난 경우, 예외 발생
            if (expirationTime == null || expirationTime.before(new Date())) {
                // 사용할 수 없는 토큰
                throw new InvalidJwtTokenException("JWT가 만료되었습니다.");
            }

            return jwtClaimsSet;
        } catch (ParseException e) {
            // JWT 문자열 파싱 실패나 서명 검증 객체 처리 중 오류가 발생할 경우, 예외 발생
            throw new InvalidJwtTokenException("JWT 파싱에 실패했습니다.", e);
        } catch (JOSEException e) {
            throw new InvalidJwtTokenException("JWT 검증에 실패했습니다.", e);
        }
    }

    // JWT 서명에 사용할 secret key를 UTF-8 byte 배열로 변환
    private byte[] getJwtSecretKeyBytes() {
        return jwtProperties.getJwtSecretKey().getBytes(StandardCharsets.UTF_8);
    }

    // claims의 토큰 타입이 Access Token인지 검증
    private void validateAccessToken(JWTClaimsSet jwtClaimsSet) {
        try {
            String tokenType = jwtClaimsSet.getStringClaim(TOKEN_TYPE);

            // claims의 token_type이 access_token이 아닐 경우, 예외 발생
            if (!ACCESS_TOKEN_TYPE.equals(tokenType)) {
                throw new InvalidAccessTokenException();
            }
        } catch (ParseException e) {
            // token_type claim의 형식이 잘못된 경우, 파싱 처리 오류로 예외 발생
            throw new InvalidAccessTokenException(e);
        }
    }

    // claims의 토큰 타입이 Refresh Token인지 검증
    private void validateRefreshToken(JWTClaimsSet jwtClaimsSet) {
        try {
            String tokenType = jwtClaimsSet.getStringClaim(TOKEN_TYPE);

            // claims의 token_type이 refresh_token이 아닐 경우, 예외 발생
            if (!REFRESH_TOKEN_TYPE.equals(tokenType)) {
                throw new InvalidRefreshTokenException();
            }
        } catch (ParseException e) {
            // token_type claim의 형식이 잘못된 경우, 파싱 처리 오류로 예외 발생
            throw new InvalidRefreshTokenException(e);
        }
    }
}
