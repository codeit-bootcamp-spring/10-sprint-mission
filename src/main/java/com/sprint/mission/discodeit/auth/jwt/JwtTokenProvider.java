package com.sprint.mission.discodeit.auth.jwt;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.sprint.mission.discodeit.auth.DiscodeitUserDetails;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.entity.enums.Role;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.Collections;
import java.util.Date;
import java.util.UUID;

@Component
@Slf4j
public class JwtTokenProvider {
    @Getter
    @Value("${jwt.key}")
    private String secretKey;

    @Getter
    @Value("${jwt.access-token-expiration-minutes}")
    private int accessTokenExpirationMinutes;

    @Getter
    @Value("${jwt.refresh-token-expiration-minutes}")
    private int refreshTokenExpirationMinutes;

    public String generateAccessToken(Authentication authentication){
        try{
            // 비밀키 준비
            JWSSigner signer = new MACSigner(secretKey.getBytes(StandardCharsets.UTF_8));

            // 인증 객체에서 정보를 꺼냄
            DiscodeitUserDetails userDetails = (DiscodeitUserDetails) authentication.getPrincipal();
            UserDto userDto = userDetails.getUserDto();
            String role = authentication.getAuthorities().iterator().next().getAuthority();

            // 페이로드 준비
            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                    .subject(userDto.getEmail())
                    .claim("role", role)
                    .claim("id", userDto.getId().toString())
                    .claim("username", userDto.getUsername())
                    .issueTime(new Date())
                    .expirationTime(new Date(System.currentTimeMillis() + accessTokenExpirationMinutes * 60 * 1000 ))
                    .build();

            // 헤더와 페이로드를 합쳐서 서명 만듬
            SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);
            signedJWT.sign(signer);

            return signedJWT.serialize();

        } catch (JOSEException e){
            log.error("JWT 토큰 생성 중 에러 발생", e);
            throw new RuntimeException("토큰 생성 실패");
        }
    }

    public String generateRefreshToken(Authentication authentication){
        try {
            JWSSigner signer = new MACSigner(secretKey.getBytes());

            DiscodeitUserDetails userDetails = (DiscodeitUserDetails) authentication.getPrincipal();
            UserDto userDto = userDetails.getUserDto();
            String role = authentication.getAuthorities().iterator().next().getAuthority();

            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                    .subject(userDto.getEmail())
                    .claim("role", role)
                    .claim("id", userDto.getId().toString())
                    .claim("username", userDto.getUsername())
                    .issueTime(new Date())
                    .expirationTime(new Date(System.currentTimeMillis() + refreshTokenExpirationMinutes * 60 * 1000))
                    .build();

            SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);
            signedJWT.sign(signer);
            return signedJWT.serialize();
        } catch (JOSEException e) {
            throw new RuntimeException("리프레시 토큰 생성 실패", e);
        }
    }

    public boolean validateToken(String token){
        try {
            // 문자열로 받은 토큰 nimbus객체로 변환
            SignedJWT signedJWT = SignedJWT.parse(token);

            // 서명 검증기 생성
            JWSVerifier verifier = new MACVerifier(secretKey.getBytes(StandardCharsets.UTF_8));

            // 유효한 토큰인지 검증(만료일, 검증기 일치)
            boolean isSignatureValid = signedJWT.verify(verifier);
            boolean isNotExpired = new Date().before(signedJWT.getJWTClaimsSet().getExpirationTime());

            return isNotExpired && isSignatureValid;

        } catch (ParseException | JOSEException e){
            log.error("유효하지 않은 JWT 토큰입니다.", e);
            return false;
        }
    }

    public Authentication getAuthentication(String token){
        try{
            SignedJWT signedJWT = SignedJWT.parse(token);
            JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();

            // 토큰에 저장한 정보 꺼냄
            String email = claimsSet.getSubject();
            String roleString = claimsSet.getStringClaim("role");
            Role role = Role.valueOf(getSimpleRole(roleString));
            UUID id = UUID.fromString(claimsSet.getStringClaim("id"));
            String username = claimsSet.getStringClaim("username");

            SimpleGrantedAuthority authority = new SimpleGrantedAuthority(roleString);

            UserDto userDto = new UserDto(id, username, email, null, role, true);
            DiscodeitUserDetails userDetails = new DiscodeitUserDetails(userDto, null);

            return new UsernamePasswordAuthenticationToken(userDetails, null, Collections.singleton(authority));
        } catch (ParseException e) {
            throw new RuntimeException("토큰에서 정보를 추출할 수 없습니다");
        }
    }

    public String getEmailFromToken(String token){
        try{
            SignedJWT signedJWT = SignedJWT.parse(token);
            JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();

            return claimsSet.getSubject();
        } catch (ParseException e){
            throw new RuntimeException("토큰에서 이메일을 추출할 수 없습니다");
        }
    }

    private String getSimpleRole(String role){
        return role.substring(5);
    }
}
