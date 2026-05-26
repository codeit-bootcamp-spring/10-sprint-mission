package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.auth.jwt.JwtTokenProvider;
import com.sprint.mission.discodeit.dto.authDto.jwt.JwtDto;
import com.sprint.mission.discodeit.dto.authDto.jwt.TokenResultDto;
import com.sprint.mission.discodeit.entity.RefreshToken;
import com.sprint.mission.discodeit.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public TokenResultDto reissueToken(String oldRefreshToken){
        String email = jwtTokenProvider.getEmailFromToken(oldRefreshToken);

        // 서명 및 만료일 검증
        if(!jwtTokenProvider.validateToken(oldRefreshToken)){
            throw new IllegalArgumentException("유효하지 않은 토큰입니다");
        }

        // db에 있는지 확인
        RefreshToken savedToken = refreshTokenRepository.findByToken(oldRefreshToken).orElse(null);
        // 없으면 이미 사용되어서 폐기된 토큰임
        if(savedToken == null){
            // email로 해당 유저의 토큰을 모두 삭제하기
            refreshTokenRepository.deleteByEmail(email);
            throw new IllegalArgumentException("토큰 재사용 탐지로 강제 로그아웃");
        }

        Authentication authentication = jwtTokenProvider.getAuthentication(oldRefreshToken);
        String newAccessToken = jwtTokenProvider.generateAccessToken(authentication);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(authentication);

        savedToken.updateToken(newRefreshToken);

        return new TokenResultDto(newAccessToken, newRefreshToken, email);
    }
}
