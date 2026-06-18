package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.auth.DiscodeitUserDetails;
import com.sprint.mission.discodeit.auth.jwt.JwtInformation;
import com.sprint.mission.discodeit.auth.jwt.JwtRegistry;
import com.sprint.mission.discodeit.auth.jwt.JwtTokenProvider;
import com.sprint.mission.discodeit.dto.authDto.jwt.JwtDto;
import com.sprint.mission.discodeit.dto.authDto.jwt.TokenResultDto;
import com.sprint.mission.discodeit.entity.RefreshToken;
import com.sprint.mission.discodeit.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtRegistry jwtRegistry;

    @Transactional(noRollbackFor = IllegalArgumentException.class)
    public TokenResultDto reissueToken(String oldRefreshToken){
        // 서명 및 만료일 검증
        if(!jwtTokenProvider.validateToken(oldRefreshToken)){
            throw new IllegalArgumentException("유효하지 않은 토큰입니다");
        }

        String email = jwtTokenProvider.getEmailFromToken(oldRefreshToken);
        Authentication authentication = jwtTokenProvider.getAuthentication(oldRefreshToken);
        DiscodeitUserDetails userDetails = (DiscodeitUserDetails) authentication.getPrincipal();

        // 2. DB에 있는지 확인
        RefreshToken savedToken = refreshTokenRepository.findByToken(oldRefreshToken).orElse(null);

        // 3. 없으면 이미 사용되어서 폐기된 토큰임 (토큰 탈취/재사용 탐지)
        if (savedToken == null) {
            log.warn("🚨 토큰 재사용 탐지! email: {}", email);

            // DB에서 해당 유저의 모든 리프레시 토큰 삭제
            refreshTokenRepository.deleteByEmail(email);

            // 메모리(Registry)에서도 해당 유저의 세션을 완벽히 날려버려야 로그아웃
            jwtRegistry.invalidateJwtInformationByUserId(userDetails.getUserDto().getId());

            throw new IllegalArgumentException("토큰 재사용 탐지로 강제 로그아웃");
        }

        // 4. 새 토큰 발급
        String newAccessToken = jwtTokenProvider.generateAccessToken(authentication);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(authentication);

        // 5. DB 업데이트
        savedToken.updateToken(newRefreshToken);

        // 옛날 리프레시 토큰을 가진 장부 기록을 찾아서, 새로 발급된 토큰 정보로 덮어씌웁니다.
        JwtInformation newJwtInfo = new JwtInformation(userDetails.getUserDto(), newAccessToken, newRefreshToken);
        jwtRegistry.rotateJwtInformation(oldRefreshToken, newJwtInfo);

        log.info("✅ 토큰 재발급 및 Registry 로테이션 완료. email: {}", email);

        return new TokenResultDto(newAccessToken, newRefreshToken, email);
    }
}
