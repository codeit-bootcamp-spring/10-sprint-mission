package com.sprint.mission.discodeit.security.jwt;

import com.sprint.mission.discodeit.dto.response.UserDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

/*
    JwtInformation
    --------------
    로그인한 사용자의 인증 정보 및 현재 유효한 JWT 토큰 쌍을 관리하는 데이터 객체
 */
@Getter
@AllArgsConstructor
public class JwtInformation {
    private UserDto userDto;
    private String accessToken;
    private String refreshToken;

    // 토큰 로테이션 발생 시, 토큰 값 갱신
    public void rotate(String newAccessToken, String newRefreshToken) {
        this.accessToken = newAccessToken;
        this.refreshToken = newRefreshToken;
    }
}